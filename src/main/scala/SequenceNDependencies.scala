import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Future, _}
import scala.io._

// these ones are all limited by the maximum tuple of 22 or so, or the maximum applicative builder of 12 or so
object SequenceTwoDependencies extends App {

  val inFile = Future(StdIn.readLine("Filename: ")).map { passthru => println(passthru); passthru }
  val search = inFile.flatMap { _ => Future(StdIn.readLine("Search: ")).map { passthru => println(passthru); passthru } }
  val source = inFile.flatMap { in => Future(Source.fromFile(in).getLines) }

  val result = search.zip(source).map {
    case (searchVal, sourceVal) =>
      sourceVal.contains(searchVal)
  }

  val awaited = Await.result(result, Duration.Inf)
  println(s"search in file?: $awaited")

}

object SequenceThreeDependencies extends App {

  def sequence3[A,B,C](a: Future[A], b: Future[B], c: Future[C]) = {
    a.zip(b).zipWith(c) {
      case ((a, b), c) => (a, b, c)
    }
  }

  val inFile = Future(StdIn.readLine("Filename: ")).map { passthru => println(passthru); passthru }
  val search = inFile.flatMap { _ => Future(StdIn.readLine("Search: ")).map { passthru => println(passthru); passthru } }
  val write = search.flatMap { _ => Future(StdIn.readLine("thing to print if found: ")).map { passthru => println(passthru); passthru } }
  val source = inFile.flatMap { in => Future(Source.fromFile(in).getLines) }

  val result = sequence3(search, source, write).map {
    case (searchVal, sourceVal, writeVal) =>
      if (sourceVal.contains(searchVal)) s"FOUND: $writeVal" else "NOT FOUND!"
  }

  val awaited = Await.result(result, Duration.Inf)
  println(s"found in file?: $awaited")

}

object SequenceThreeDependenciesApplicative extends App {

  import cats.syntax.cartesian._
  import cats.instances.future._

  val inFile = Future(StdIn.readLine("Filename: ")).map { passthru => println(passthru); passthru }
  val search = inFile.flatMap { _ => Future(StdIn.readLine("Search: ")).map { passthru => println(passthru); passthru } }
  val write = search.flatMap { _ => Future(StdIn.readLine("thing to print if found: ")).map { passthru => println(passthru); passthru } }
  val source = inFile.flatMap { in => Future(Source.fromFile(in).getLines) }

  val result = (search |@| source |@| write).map {
    case (searchVal, sourceVal, writeVal) =>
      if (sourceVal.contains(searchVal)) s"FOUND: $writeVal" else "NOT FOUND!"
  }

  val awaited = Await.result(result, Duration.Inf)
  println(s"found in file?: $awaited")

}
