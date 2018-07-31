import scala.io.Source

object Scissors extends App {

  val input: Iterator[String] = Source.stdin.getLines
  val output: String => Unit = println(_)

  sealed trait Player
  case object Human extends Player
  case object AutoRandom extends Player
  case object AutoTactical extends Player

  sealed trait Move
  case object Rock extends Move
  case object Paper extends Move
  case object Scissors extends Move
  val beatenByOrder = List(Rock, Paper, Scissors) // n+1 always beats n (mod 3)

  def play(input: Iterator[String], output: String => Unit) = {
    output("choose player 1")
    val game = for {
      player1 <- {
        output("choose player 1")
        getPlayer(input, output)
      }
      player2 <- {
        output("choose player 2")
        getPlayer(input, output)
      }
      game <- runGame(player1, player2, input, output)
    } yield game
    output(game.map(_ => "game finished").getOrElse("game ended early"))
  }

  def runGame(player1: Player, player2: Player, input: Iterator[String], output: String => Unit): Option[Unit] = {
    def runGame0(p1Score: Int, p2Score: Int): Option[Int] = {
      if (p1Score == 2) Some(1)
      else if (p2Score == 2) Some(2)
      else {
        p2Won(player1, player2, input, output).flatMap { round =>
          round match {
            case WPlayer2 => runGame0(p1Score, p2Score + 1)
            case WPlayer1 => runGame0(p1Score + 1, p2Score)
            case Draw => runGame0(p1Score, p2Score)
          }
        }
      }
    }
    runGame0(0, 0).map(won => output(s"player that won: ${won}") )
  }

  sealed trait Winner
  case object WPlayer1 extends Winner
  case object WPlayer2 extends Winner
  case object Draw extends Winner

  def secondMoveWon(p1Move: Move, p2Move: Move): Winner = {
    println(s"p1Move: $p1Move beatenByOrder: $beatenByOrder")
    val p1Index = beatenByOrder.indexOf(p1Move)
    val p2Index = beatenByOrder.indexOf(p2Move)
    val beat = (p2Index - p1Index) % beatenByOrder.length
    beat match {
      case 1 => WPlayer2
      case 0 => Draw
      case -1 => WPlayer1
    }
  }

  def p2Won(player1: Player, player2: Player, input: Iterator[String], output: String => Unit): Option[Winner] = {
    for {
      p1Move <- getMove(player1, input, output)
      p2Move <- getMove(player2, input, output)
    } yield secondMoveWon(p1Move, p2Move)

  }

  def getMove(player: Player, input: Iterator[String], output: String => Unit): Option[Move] = {
    player match {
      case Human => {
        output("what move?")
        getMoveInput(input, output)
      }
      case AutoRandom =>
        Some(Scissors)//TODO
      case AutoTactical =>
        Some(Scissors)//TODO
    }
  }

  private def getPlayer(input: Iterator[String], output: String => Unit) = {
    output("enter choice and press return - 1:human 2:random 3:tactical")
    input.collectFirst {
      case "1" => Human
      case "2" => AutoRandom
      case "3" => AutoTactical
    }
  }

  private def getMoveInput(input: Iterator[String], output: String => Unit) = {
    output("enter choice and press return - r:rock p:peper s:scissors")
    input.collectFirst {
      case "r" => Rock
      case "p" => Paper
      case "s" => Scissors
    }
  }

  play(input, output)

}
