import TailRec.pal

import scala.annotation.tailrec

object Rec {
  def pal(string: String): Boolean =
    pal0(string.toCharArray.toVector, canDrop = true)

  def pal0(chars: Vector[Char], canDrop: Boolean): Boolean =
    if (chars.length <= 1)
      true
    else {
      val firstLastMatch = chars.head == chars.last
      val middleIsPal = pal0(chars.tail.init, canDrop)
      val isPalWithoutDroppingHere = firstLastMatch && middleIsPal

      def leftDroppedPal = pal0(chars.tail, canDrop = false)

      def rightDroppedPal = pal0(chars.init, canDrop = false)

      isPalWithoutDroppingHere || (canDrop && (leftDroppedPal || rightDroppedPal))
    }

}
object TailRec {

  def pal(string: String): Boolean =
    pal0(string.toCharArray.toVector, CanDrop)

  sealed trait Remainder
  case object CanDrop extends Remainder
  case object AlreadyDropped extends Remainder
  case class AlreadyDroppedStillToTry(tryMe: Vector[Char]) extends Remainder

  @tailrec
  def pal0(chars: Vector[Char], remainder: Remainder): Boolean =
    if (chars.length <= 1)
      true
    else {
      val firstLastMatch = chars.head == chars.last

      if (firstLastMatch) {
        pal0(chars.tail.init, remainder)
      } else remainder match {
        case CanDrop =>
          pal0(chars.tail, AlreadyDroppedStillToTry(chars.init))
        case AlreadyDroppedStillToTry(hi) =>
          pal0(hi, AlreadyDropped)
        case AlreadyDropped =>
          false
      }
    }

}

pal("asa")
pal("assa")
pal("asdf")
pal("asas")
pal("as")
