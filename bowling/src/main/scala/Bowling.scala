
object Bowling extends App {

  def getPotentialFrames(game: List[Int]): List[List[Int]] = {
    (game.map(Some.apply) ++ List(None, None)).sliding(3).toList.map(_.flatten)
  }

  def getActualFrameStarts(frames: List[List[Int]]) = {

    case class FromPrev(
      nextIsStartOfFrame: Boolean,
      reversedFrameStarts: List[List[Int]]
    )

    val initialValue = FromPrev(nextIsStartOfFrame = true, reversedFrameStarts = List[List[Int]]())

    val finalState = frames.foldLeft(initialValue)({
      case (fromPrev, next) if fromPrev.nextIsStartOfFrame =>
        val newFrameStarts = next :: fromPrev.reversedFrameStarts
        val nextIsIn = next.head == 10
        FromPrev(nextIsIn, newFrameStarts)
      case (fromPrev, _) =>
        fromPrev.copy(nextIsStartOfFrame = true)
    })

    finalState.reversedFrameStarts.reverse
  }

  def removeUnnecessary(frame: List[Int]): List[Int] = {
    frame match {
      case f :: s :: _ :: Nil if f + s < 10 =>
        f :: s :: Nil
      case all => all
    }
  }

  def isCompleteFrame(frame: List[Int]): Boolean = {
    frame match {
      case _ :: _ :: _ :: _ => true
      case f :: s :: _ if f + s < 10 => true
      case _ => false
    }
  }

  def isGameOver[A](frames: List[A]): Boolean = {
    frames.length >= 10
  }

  // tests

  val testGame = List(
    1,2, 10, 4,5, 2,8,
    7,3, 10, 10, 2,2,
    1, 8, 6, 3)

  private val potentialFrames: List[List[Int]] = getPotentialFrames(testGame)
  println(potentialFrames)

  private val actualFrameStarts: List[List[Int]] = getActualFrameStarts(potentialFrames)
  println(actualFrameStarts)

  private val framesWithoutUnnecessary: List[List[Int]] = actualFrameStarts.map(removeUnnecessary)
  println(framesWithoutUnnecessary)

  private val completeFrames: List[List[Int]] = framesWithoutUnnecessary.filter(isCompleteFrame)
  println(completeFrames)

  private val scoreEachFrame: List[Int] = completeFrames.map(_.sum)
  println(scoreEachFrame)

  println("total score: " + scoreEachFrame.sum)

  println("game over? : "+ isGameOver(completeFrames))

}

