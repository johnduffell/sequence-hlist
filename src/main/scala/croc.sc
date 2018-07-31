import scala.collection.immutable.Stream.Cons

def map(ints: => Stream[Int], op: Int => Int): Stream[Int] = {
  ints match {
    case Stream.Empty => Stream.empty
    case cons: Cons[Int] =>
      cons.head #:: map(cons.tail, op)
  }
}

type RestToWholeStream[A] = Stream[A] => Stream[A]

def foldLeft(
  ints: => Stream[Int],
  accu: Boolean,
  op: (Boolean, Int) => (Boolean, Int)
): Stream[Int] = {
  ints match {
    case Stream.Empty => Stream.empty
    case cons: Cons[Int] =>
      lazy val (nextAcc, mapped) = op(accu, cons.head)
      mapped #:: foldLeft(cons.tail, nextAcc, op)
  }
}

val st = Range(1, 10).toStream.map { int =>
  println(s"INT: $int")
  int
}

st

//val mapped = map(st, identity)
val foldLefted = foldLeft(st, false, {
  case (accu, int) => (accu, int)
})
//st.foldRight(Stream[Int]()){
//  case (int, acc) => int #:: acc
//}


st

foldLefted.toList