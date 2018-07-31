import scala.util.Try

// either type
//case class Either[A, B, C](f: (A => C, B => C) => C)
//def leftUpcast[A, B, C](left: A): Either[A, B, C] =
//  Either((elimL, elimR) => elimL(left))
//def rightUpcast[A, B, C](right: B): Either[A, B, C] =
//  Either((elimL, elimR) => elimR(right))
//
//val num = "2"
//val sum = if (Try(Integer.parseInt(num)).isSuccess) {
//  rightUpcast(2)
//} else {
//  leftUpcast("error")
//}
//sum.f(l => s"left$l", r => s"right$r")

type bool[A] = A => A => A
def true_[A]: bool[A] = ifTrue => ifFalse => ifTrue
def false_[A]: bool[A] = ifTrue => ifFalse => ifFalse

def same[A](i1: List[Unit], i2: List[Unit]): bool[A] =
  (i1, i2) match {
    case (Nil, Nil) => true_
    case (Nil, _) => false_
    case (_, Nil) => false_
    case (n1, n2) => same[A](n1, n2)
  }

same(Nil, List(())) {
  "true!!"
} {
  "false!!"
}

// product (pair)
final class C[A, B](a: A, b: B) {

  def _1: A = a

  def _2: B = b

}