def balanced(str: String): Boolean = {

  val rules = Map(
    '(' -> ')',
    '[' -> ']',
    '{' -> '}'
  )

  def rec(toMatch: List[Char], outstanding: List[Char]): Boolean = {
    (toMatch, outstanding) match {
      case (Nil, Nil) => true
      case (nextToMatch :: otherToMatch, nextOutstanding :: otherOutstanding) if nextToMatch == nextOutstanding =>
        rec(otherToMatch, otherOutstanding)
      case (nextToMatch :: otherToMatch, outstanding) if rules.contains(nextToMatch) =>
        rec(otherToMatch, rules.apply(nextToMatch) :: outstanding)
      case _ => false
    }
  }

  rec(str.toCharArray.toList, Nil)

}

balanced("()[]]")