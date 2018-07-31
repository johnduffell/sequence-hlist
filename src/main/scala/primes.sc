val limit = 1000000

def findNext(alreadyKnown: List[Int], candidates: List[Int], maxRoot: Int): List[Int] = {

  def add(nextToRemove: Int, current: Int, firstPrime: Int): Int = {
    if (nextToRemove < current) {
      add(nextToRemove + firstPrime, current, firstPrime)
    } else {
      nextToRemove
    }
  }

  candidates match {
    case firstPrime :: restCandidates if firstPrime <= maxRoot =>

      val newCandidates = restCandidates.foldLeft((firstPrime, List[Int]()))({
        case ((nextToRemove, sofar), current) =>
          val reallyNextToRemove = add(nextToRemove, current, firstPrime)
          if (reallyNextToRemove == current) {
            (reallyNextToRemove + firstPrime, sofar)
          } else {
            (reallyNextToRemove, current :: sofar)
          }
      })._2.reverse

      findNext(firstPrime :: alreadyKnown, newCandidates, maxRoot)
    case rest => alreadyKnown.reverse ++ rest
  }

}

val primes = findNext(List(), (2 to limit).toList, scala.math.sqrt(limit.toDouble).toInt)

primes.length // 168