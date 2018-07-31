import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

val a: Int => List[Int] = in => List()

for {
  first <- List(1, 2)
  second <- a(first)
  _ = println("ahahaha")
} yield second

for {
  (first, last) <- Some((1,2))
} yield s"first: $first, last: $last"

val (first, last) = (1,2)

val aa = for {
  x <- Future { println(112); 12 }
  y <- Future { println(214); 12 }
} yield (x, y)

Await.result(aa, 10.seconds)

val m = Map(1 -> "hi", 2 -> "bye")

m.map({ case (i, i2) => (i, i2)})

def addTuple(i: Int, i2: Int): Int = i + i2

(addTuple _).tupled((2,3))

case class Hi(i: Int, i2: Int)

def addTuplea: Hi => Int = {
  case Hi(i, i2) => i + i2
}

addTuplea(Hi(2,3))

m.map({case (i, s) => i})