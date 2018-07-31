import scala.collection.immutable

def permL(string: String): Int = {
  perm0(string.toCharArray.toList.groupBy(a => a).mapValues(_.length)).map(chars => new String(chars.toArray)).length
}

def perm0(charFreqs: Map[Char, Int]): List[List[Char]] = {
  if (charFreqs.isEmpty)
    List(Nil)
  else
  charFreqs.toList.flatMap {
    case (char, freq) =>
      val updated = if (freq == 1) charFreqs.-(char) else charFreqs.updated(char, freq - 1)
      val rest = perm0(updated)
      rest.map(perm => char :: perm)
  }
}

def perm(string: String) = (permL(string), permQ(string))

perm("asdf")
perm("asdd")
perm("aass")
perm("aaas")
perm("asdfg")
perm("as")
perm("aa")
perm("aaasa")

4*3

def fact(n: Int): Int =
  if (n <= 1)
    1
else
    n * fact(n - 1)

def permQ(string: String): Int = {
  val counts = string.groupBy(a => a).map(_._2.length)
  val perms = counts.map(fact)
  val totalPossibilities = fact(string.length)
    perms.foldLeft(totalPossibilities) {
      case (total, nextDiv) =>
        total / nextDiv
    }
}