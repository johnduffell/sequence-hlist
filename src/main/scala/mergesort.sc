import scala.annotation.tailrec
import scala.util.Random

val numbers = Array.fill(10)(Random.nextInt(1000))

//def max(list: Array[Int]): Option[Int] = {
//  if (list.length <= 1)
//    list.headOption
//  else {
//    val len = list.length
//    val (listA, listB) = list.splitAt(list.length / 2)
//    val maxA = max(listA)
//    val maxB = max(listB)
//    if (maxA > maxB)
//      maxA
//    else
//      maxB
//  }
//}

//max(numbers)

def sort(list: Array[Int]): List[Int] = {

  @tailrec
  def mergeT(a: List[Int], b: List[Int], sofar: List[Int]): List[Int] =
    (a, b) match {
      case (a, Nil) => sofar.reverse ++ a
      case (Nil, b) => sofar.reverse ++ b
      case (ah :: atail, bh :: btail) if ah < bh =>
        mergeT(atail, bh :: btail, ah :: sofar)
      case (a, bh :: btail) =>
        mergeT(a, btail, bh :: sofar)
    }

  def merge(a: List[Int], b: List[Int]): List[Int] =
    (a, b) match {
      case (a, Nil) => a
      case (Nil, b) => b
      case (ah :: atail, bh :: btail) if ah < bh =>
        ah :: merge(atail, bh :: btail)
      case (a, bh :: btail) =>
        bh :: merge(a, btail)
    }

  if (list.length <= 1)
    list.toList
  else {
    val len = list.length
    val (listA, listB) = list.splitAt(list.length / 2)
    val maxA = sort(listA)
    val maxB = sort(listB)
    mergeT(maxA, maxB, Nil)
  }
}

sort(numbers)