import cats.Applicative
import cats.instances.future._
import shapeless.{::, HList, HNil}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Future, _}
import scala.io._

// this is only limited by how slow compile times you like
object SequenceHlist {

  sealed trait CanSequence[APPLICATIVE[_], TRAVERSABLE_APPLICATIVES, SEQUENCED] {
    def sequence(from: TRAVERSABLE_APPLICATIVES): SEQUENCED
  }

  implicit def canSequenceHNil[APPLICATIVE[_] : Applicative]
  : CanSequence[APPLICATIVE, HNil, APPLICATIVE[HNil]] =
    new CanSequence[APPLICATIVE, HNil, APPLICATIVE[HNil]] {
      override def sequence(from: HNil): APPLICATIVE[HNil] =
        implicitly[Applicative[APPLICATIVE]].pure(HNil)
    }

  implicit def canSequenceHCons[APPLICATIVE[_] : Applicative, HEAD, TAIL <: HList, TAILUNWRAPPED_INFERRED <: HList]
  (implicit
    canSequenceTail: CanSequence[APPLICATIVE, TAIL, APPLICATIVE[TAILUNWRAPPED_INFERRED]]
  ): CanSequence[APPLICATIVE, ::[APPLICATIVE[HEAD], TAIL], APPLICATIVE[::[HEAD, TAILUNWRAPPED_INFERRED]]] =
    new CanSequence[APPLICATIVE, ::[APPLICATIVE[HEAD], TAIL], APPLICATIVE[::[HEAD, TAILUNWRAPPED_INFERRED]]] {
      override def sequence(from: ::[APPLICATIVE[HEAD], TAIL]): APPLICATIVE[::[HEAD, TAILUNWRAPPED_INFERRED]] = {
        val doneRest: APPLICATIVE[TAILUNWRAPPED_INFERRED] = canSequenceTail.sequence(from.tail)
        val applicative = implicitly[Applicative[APPLICATIVE]]
        applicative.map2(from.head, doneRest)({ case (head, rest) => head :: rest })
      }
    }

  implicit class hlistOfApplicatives[APPLICATIVE[_], APPLICATIVES <: HList](
    applicatives: APPLICATIVE[_] :: APPLICATIVES
  ) {
    def sequence[SEQUENCED <: APPLICATIVE[HList]](implicit
      app: Applicative[APPLICATIVE],
      e: CanSequence[APPLICATIVE, APPLICATIVE[_] :: APPLICATIVES, SEQUENCED]
    ): SEQUENCED =
      e.sequence(applicatives)
  }

}

//object UseHlistDemoApp extends App {
//
//  import SequenceHlist._
//
//  val inFile = Future(StdIn.readLine("Filename: ")).map { passthru => println(passthru); passthru }
//  val search = inFile.flatMap { _ => Future(StdIn.readLine("Search: ")).map { passthru => println(passthru); passthru } }
//  val source = inFile.flatMap { in => Future(Source.fromFile(in).getLines) }
//
//  val result = (search :: source :: HNil).sequence.map {
//    case (searchVal :: sourceVal :: HNil) =>
//      sourceVal.contains(searchVal)
//  }
//
//  val awaited = Await.result(result, Duration.Inf)
//  println(s"Found in file?: $awaited")
//
//}
