//
//import scala.io._
//
//// this is only limited by how slow compile times you like
//object ProveReductive {
//
//  sealed trait CanSequence[APPLICATIVE[_], TRAVERSABLE_APPLICATIVES, SEQUENCED] {
//    def sequence(from: TRAVERSABLE_APPLICATIVES): SEQUENCED
//  }
//
//  implicit def canSequenceNil[APPLICATIVE[_] : Applicative]
//  : CanSequence[APPLICATIVE, Nil, APPLICATIVE[HNil]] =
//    new CanSequence[APPLICATIVE, HNil, APPLICATIVE[HNil]] {
//      override def sequence(from: HNil): APPLICATIVE[HNil] =
//        implicitly[Applicative[APPLICATIVE]].pure(HNil)
//    }
//
//  implicit def canSequenceCons[APPLICATIVE[_] : Applicative, HEAD, TAIL <: HList, TAILUNWRAPPED_INFERRED <: HList]
//  (implicit
//    canSequenceTail: CanSequence[APPLICATIVE, TAIL, APPLICATIVE[TAILUNWRAPPED_INFERRED]]
//  ): CanSequence[APPLICATIVE, ::[APPLICATIVE[HEAD], TAIL], APPLICATIVE[::[HEAD, TAILUNWRAPPED_INFERRED]]] =
//    new CanSequence[APPLICATIVE, ::[APPLICATIVE[HEAD], TAIL], APPLICATIVE[::[HEAD, TAILUNWRAPPED_INFERRED]]] {
//      override def sequence(from: ::[APPLICATIVE[HEAD], TAIL]): APPLICATIVE[::[HEAD, TAILUNWRAPPED_INFERRED]] = {
//        val doneRest: APPLICATIVE[TAILUNWRAPPED_INFERRED] = canSequenceTail.sequence(from.tail)
//        val applicative = implicitly[Applicative[APPLICATIVE]]
//        applicative.map2(from.head, doneRest)({ case (head, rest) => head :: rest })
//      }
//    }
//
//  implicit class foldableList[A, REST <: List[A]](
//    list: A :: REST
//  ) {
//    def foldLeft[B, SEQUENCED <: List[A]](z: B)(op: (B, A) => B)(implicit
//      e: CanSequence[APPLICATIVE, APPLICATIVE[_] :: REST, SEQUENCED]
//    ): B =
//      e.sequence(list)
//
//
//    def foldLeftO[B](z: B)(op: (B, A) => B): B = {
//      var acc = z
//      var these = this
//      while (!these.isEmpty) {
//        acc = op(acc, these.head)
//        these = these.tail
//      }
//      acc
//    }
//  }
//
//}
//
//object ReductiveDemoApp extends App {
//
//  import ProveReductive._
//
//  def sum(list: List[Int]): Int =
//    list match {
//      case Nil => 0
//      case first :: rest => first + sum(rest)
//    }
//
//  println(s"sum: ${sum(List(1,2))}")
//
//}
