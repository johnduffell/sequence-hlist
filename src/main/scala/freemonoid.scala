

// turn any set A into a free monoid

sealed trait Free[+A]{
  def associativeCombinationOrder: List[A]
}

// to make a  monoid, we have to add a new element to the set that represents identity
// Identity is that element
case object Identity extends Free[Nothing] {
  val associativeCombinationOrder: List[Nothing] = List()
}
// a monoid also might have a load of other elements, however there may actually be only identity (e.g if A is Void)
// the non identity elements consist of the ones that came directly from A and all the elements where other non identity elements have been combined
// however since we want to identify (a . b) . c with a . (b . c) we only want to store the order, not the precedence
// so when we flatten a binary tree, we end up with a list.
case class Combined[A](associativeCombinationOrder: List[A]) extends Free[A]

object Free {

  // this is to take any value from set A and turn it into a value in the monoid set
  // the monoid set is bigger than the set a, as since it is a free monoid, it needs a unique value for every combination of
  // two existing elements
  def aToFree[A](a: A): List[A] = List(a)

  def freeOp[A](a1: List[A], a2: List[A]): List[A] =
    a1 ++ a2

}

object TestFree extends App {
  sealed trait MyAlgebra
  case object First extends MyAlgebra
  case object Second extends MyAlgebra

  val first = Free.aToFree(First)
  val second = Free.aToFree(Second)

  val program: Free[MyAlgebra] = List(first, second, first)
    .foldLeft[Free[MyAlgebra]](Identity)(???/*Free.freeOp*/)

  println(s"program: $program")
  // now need a transformation from MyAlgebra to numbers


  def transform(myAlgebra: MyAlgebra): Int =
    myAlgebra match {
      case First => 1
      case Second => 2
    }
  val intIdentity = 0
  def intOperation(a: Int, b: Int) = a + b


  val result = program.associativeCombinationOrder.map(transform).fold(intIdentity)(intOperation)

  println(s"result: $result")



  val intIdentity2 = 1
  def intOperation2(a: Int, b: Int) = a * b

  val result2 = program.associativeCombinationOrder.map(transform).fold(intIdentity2)(intOperation2)

  println(s"result2: $result2")

}