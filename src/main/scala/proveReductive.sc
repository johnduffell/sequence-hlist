sealed trait Reducible[LIST <: List[Int]] {
  def reduce(l: LIST): Int
}
implicit val NilReducible: Reducible[Nil.type ] = new Reducible[Nil.type] {
  override def reduce(l: Nil.type): Int = 0
}
implicit def ConsReducible[A <: List[Int]](implicit
  ev: Reducible[A]
): Reducible[Int :: A] = new Reducible[Int :: A] {
  override def reduce(l: Int :: List[Int]): List[Int] = l
}

def sum: List[Int] => Int =
  {
    case Nil => 0
    case first :: rest => first + sum(rest)
  }

def sumBad: List[Int] => Int =
  {
    case Nil => 0
    case first :: rest => first + sumBad(first :: rest)
  }

sum(List(1,2))

sumBad(List(1,2))