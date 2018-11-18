import play.api.libs.json._

object Queue extends App {

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

  override def main(args: Array[String]): Unit = {
    val end = EndStep(step2)
    val fst = TaskStep(step1, end)
    val finalLocal = InterpLocal(Initial("hello"), fst)
    println(s"final local: $finalLocal")
    val finalString = InterpJson(Initial("hello"), fst)
    println(s"final via json: $finalString")
  }

}

