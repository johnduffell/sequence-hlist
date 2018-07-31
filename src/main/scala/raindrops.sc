val defaultRules = Map(
  3 -> "plink",
  5 -> "plonk",
  7 -> "plunk"
)

def asString(rules: Map[Int, String])(num: Int): String = {
  val strings = rules.collect {
    case (factor, replacement) if (num % factor) == 0 =>
      replacement
  }
  if (strings.isEmpty)
    s"$num"
  else
    strings.mkString("")
}

val raindrops: Int => String = asString(rules = defaultRules)_

(1 to 10).map(asString(Map(2 -> "TWO")))

(1 to 10).map(asString(Map(2 -> "TWO", 3 -> "THREE")))

(1 to 106).map(raindrops)
