val list: List[Int] = Nil

list match {
  case Nil => None
  case _ :: tail => Some(tail)
}

def fb(n: Int) =
  (n % 3 == 0, n % 5 == 0) match {
  case (true, false) => "fizz"
  case (false, true) => "buzz"
  case (true, true) => "fizzbuzz"
  case _ => s"$n"
}

def opt[A](b: Boolean, a: A): Option[A] =
  if (b) Some(a) else None

def sqOpt[A](f: (A,A) => A)(l: Option[A], r: Option[A]): Option[A] =
  (l, r) match {
    case (None, None) => None
    case (optSoFar, None) => optSoFar
    case (None, maybeToConcat) => maybeToConcat
    case (Some(soFar), Some(toConcat)) => Some(f(soFar, toConcat))
  }

def fb2(n: Int) = {

  val divs = List(3 -> "fizz", 5 -> "buzz")

  divs.map { case (div, string) =>
    opt(n % div == 0, string)
  }.foldLeft(None: Option[String])(sqOpt(_ + _))
  .getOrElse(s"$n")
}

Range(1, 20).map(fb2).mkString("\n")

lazy val fib: Stream[Int] = 0 #:: 1 #:: fib.zip(fib.tail).map(a => a._1 + a._2)

fib.take(20).toList.mkString("\n")