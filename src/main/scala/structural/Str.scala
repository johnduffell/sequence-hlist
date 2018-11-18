package structural

import structural.Reads.{IntRead, StringRead}

object NeedsSome extends App {

  val myBig = Big(12, "hello", "blah")

  def apply[B: IntRead: StringRead](big: B) = s"b = ${implicitly[IntRead[B]].value(big)} and a = ${implicitly[StringRead[B]].value(big)}"

  val result = apply(myBig)

  println(s"result: $result")

}

case class Big(a: Int, b: String, c: String)
object Big {
  implicit val aReads: Reads[Big, Int] = new Reads[Big, Int] {

    override def value(big: Big): Int = big.a
  }
  implicit val bReads: Reads[Big, String] = new Reads[Big, String] {

    override def value(big: Big): String = big.b
  }

}
object Reads {

  type IntRead[FROM] = Reads[FROM, Int]
  type StringRead[FROM] = Reads[FROM, String]
}
trait Reads[FROM, TYPE] {
  def value(from: FROM): TYPE
}