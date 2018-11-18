import java.io.{InputStream, OutputStream, OutputStreamWriter}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import CompiledSteps.LambdaId
import com.amazonaws.services.lambda.runtime.Context
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

object Handler extends App {

  /*
  https://github.com/guardian/support-workers/blob/master/cloud-formation/src/templates/state-machine.yaml
  step converts json to json, and can
    retry/emailRetry
    catch
    run in parallel
    end

   */

  case class Initial(data: String)
  implicit lazy val iF: OFormat[Initial] = Json.format[Initial]

  case class NextState(moreData: String)
  implicit lazy val nF: OFormat[NextState] = Json.format[NextState]

  case class FinalState(finalData: String)
  implicit lazy val fF: OFormat[FinalState] = Json.format[FinalState]

  def step1: Initial => NextState =  { initial: Initial =>
    println("RUN STEP 1")
    NextState(initial.data)
  }

  def step2: NextState => FinalState =  { nextState: NextState =>
    println("RUN STEP 2")
    FinalState(nextState.moreData)
  }

  lazy val fst = TaskStep(step1, EndStep(step2))

  lazy val compiledSteps = InterpJson[Initial].apply(fst)

  override def main(args: Array[String]): Unit = {
    val finalLocal = InterpLocal(Initial("hello"), fst)
    println(s"final local: $finalLocal")
    println(s"compiledSteps: $compiledSteps")
    val finalString = CompiledSteps.runLocal(compiledSteps)(Initial("hello"))
    println(s"final via json: $finalString")
    val handlerFunctionName = this.getClass.getCanonicalName.replaceAll("""\$$""", "") + "::apply"
    val cfn = CompiledSteps.toCFN(compiledSteps, handlerFunctionName, ENV_VAR)
    println(s"CFN: $cfn")
    val cfnRaw = Json.prettyPrint(Json.toJson(cfn))
    println(s"CFN script: \n$cfnRaw")
    Files.write(Paths.get("step/target/generated.cfn.json"), cfnRaw.getBytes(StandardCharsets.UTF_8))
  }

  lazy val ENV_VAR: String = "LAMBDA_ID"

  // this is the entry point
  def apply(inputStream: InputStream, outputStream: OutputStream, context: Context): Unit = {
    val res = for {
      envLambdaId <- Try(System.getenv(ENV_VAR))
      lambdaId <- LambdaId.fromEnv(envLambdaId)
      output <- CompiledSteps.runSingle(compiledSteps, lambdaId, Json.parse(inputStream)) match {
        case None => Failure(new RuntimeException("oops probably couldn't deserialise"))
        case Some(result ) => Success(result)
      }
    } yield output
    res match {
      case Failure(ex) => throw ex
      case Success(outputJS) => outputForAPIGateway(outputStream, outputJS)
    }
  }

  def outputForAPIGateway(outputStream: OutputStream, jsonResponse: JsValue): Unit = {
    val writer = new OutputStreamWriter(outputStream, "UTF-8")
    println(s"Response will be: \n ${jsonResponse.toString}")
    writer.write(Json.stringify(jsonResponse))
    writer.close()
  }

}

