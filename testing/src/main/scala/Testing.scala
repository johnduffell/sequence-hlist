case class Test[Result](
  message: String,
  actual: Result,
  expected: Result,
  compare: Comparison[Result]
)
object Test {
  def equal[Result](
    message: String,
    actual: Result,
    expected: Result
  ): Test[Result] =
    Test[Result](message, actual, expected, Comparison.equal)
}

case class Comparison[Result](
  message: String,
  compare: (Result, Result) => Boolean
)
object Comparison {
  def equal[Result] = Comparison[Result]("equal", _ == _)
}

object Tests extends App {

  val test1 = Test.equal(
    "add numbers",
    1 + 1,
    2
  )

  val test2 = Test.equal(
    "add numbers wrongly",
    1 + 1,
    3
  )

  println(TestRun(test1))
  println(TestRun(test2))

}

object TestRun {

  def apply[Result](test: Test[Result]): Either[String, String] = {
    import test._
    if (compare.compare(actual, expected))
      Right(s"passed: $message")
    else
      Left(s"FAILED: $message because $actual was not ${compare.message} with $expected")
  }

}