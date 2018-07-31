

def winner(player1: String, player2: String): String = {
  val order = List("paper", "rock", "scissors")// strongest to the left
  val p1score = order.indexOf(player1)
  val p2score = order.indexOf(player2)
  val result = ((4 + p1score - p2score) % 3) - 1
  result match {
    case -1 => "p1 wins"
    case 0 => "draw"
    case 1 => "p2 wins"
  }
}

//tests

winner("scissors", "rock") //2
winner("paper", "scissors") //2
winner("rock", "paper") //2
winner("rock", "scissors") //1
winner("scissors", "paper") //2
winner("paper", "rock") // 1

winner("paper", "paper")