object Croc extends App {

  sealed trait Cell
  case object Empty extends Cell
  case object Croc extends Cell

  type River = List[List[Cell]]

  case class Position(row: Int, cell: Int)

  sealed trait Move
  case object Left extends Move
  case object Right extends Move
  case object Straight extends Move

  sealed trait Status
  case object Trying extends Status
  case object Eaten extends Status
  case object Succeeded extends Status

  ///

  val initial = Position(0, 2)
  val text = "     ,  C  ,CC CC,CC CC"

  val moves = io.Source.stdin.getLines().map {
    case "left" => Left
    case "right" => Right
    case _ => Straight
  }

  def applyMove(position: Position, move: Move) = move match {
    case Straight => Position(position.row + 1, position.cell)
    case Left => Position(position.row + 1, position.cell - 1)
    case Right => Position(position.row + 1, position.cell + 1)
  }

  def cellState(river: River, us: Position): Status = {
    if (us.row >= river.length) Succeeded
    else if (river(us.row)(us.cell) == Croc) Eaten
    else Trying
  }

  def combineState(oldState: Status, newState: Status): Status =
    oldState match {
      case Eaten => Eaten
      case Succeeded => Succeeded
      case Trying => newState
    }

  val river = parse(text)

//  moves.foldLeft((initial, {rest: Stream[Status] => (Trying: Status) #:: rest})) { case ((prevPos, soFarEaten), move) =>
//    val target = applyMove(prevPos, move)
//    (target, {rest => soFarEaten( combineState(soFarEaten, cellState(river, target))) #:: rest })
//  }

  ///

  def parse(text: String) = text.split(",").toList.map{
    _.toCharArray.toList.map {
      case 'C' => Croc
      case ' ' => Empty
    }}

  ///
  def display(river: River): String = {
    "\n" + river.map { row =>
      row.map {
        case Croc => "C"
        case Empty => " "
      }.mkString("")
    }.mkString("\n")
  }

  display(river)

  def display(river: River, us: Position): String = {
    "\n" + river.zipWithIndex.map { case (row, rowNum) =>
      row.zipWithIndex.map {
        case (Empty, cellNum) if rowNum == us.row && cellNum == us.cell => "P"
        case (Empty, _) => " "
        case (Croc, _) => "C"
      }.mkString("")
    }.mkString("\n")
  }

  display(river, initial)
}
