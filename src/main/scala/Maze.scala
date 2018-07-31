//import scala.io.StdIn

case class Action(message: Option[String], newState: Room)

sealed trait Room {
  def transitions: Map[String, Action]
}

sealed trait Puzzle {
  def initialState: Room
  def finalStates: Set[Room]
}

object Easy extends Puzzle {

  case object Hall extends Room {
    override def transitions = Map(
      "look" -> Action(Some("looking around in the hall"), Hall),
      "north" -> Action(None, Study)
    )
  }

  case object Study extends Room {
    override def transitions = Map(
      "look" -> Action(Some("there's a table"), Study),
      "south" -> Action(None, Hall),
      "look at desk" -> Action(Some("code is 1234"), Study),
      "enter 1234" -> Action(Some("well done!"), Win)
    )
  }

  case object Win extends Room {
    override def transitions = Map()
  }

  val initialState = Hall
  val finalStates: Set[Room] = Set(Win)

}

object Quit {
  def augment(puzzle: Puzzle): Puzzle = {
    puzzle
  }
}

object Maze extends App {

  puzzle(Quit.augment(Easy))

  def puzzle(puzzle: Puzzle) = {

    fromRoom(puzzle.initialState)

    def fromRoom(room: Room): Unit = {
      val command = ??? //StdIn.readLine("command> ")
      val action = room.transitions.get(command)
      val newRoom = action.map { case Action(maybeMessage, newState) =>
        maybeMessage.foreach(println)
        newState
      }.getOrElse(room)
      if (puzzle.finalStates.contains(newRoom))
        ()
      else
        fromRoom(newRoom)
    }

  }

}
