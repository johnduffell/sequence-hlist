package takeaway

import java.time.LocalDateTime

import com.twilio.Twilio
import com.twilio.`type`.PhoneNumber
import com.twilio.rest.api.v2010.account.Message
import com.typesafe.config.ConfigFactory
import takeaway.Model.{Index, Order, Quantity, Successful}

object SMSSend {
  val config = ConfigFactory.load()

  val ACCOUNT_SID = config.getString("twilio.account_sid")
  val AUTH_TOKEN = config.getString("twilio.auth_token")

  Twilio.init(ACCOUNT_SID, AUTH_TOKEN)

  val from = new PhoneNumber(config.getString("twilio.from_number"))
  val to = new PhoneNumber(config.getString("twilio.to_number")) // change this to your personal number for testing

  def apply(message: String) = {
    Message.creator(to, from, message).create()
  }

}

object Main extends App {

  println(SeeDishes())

  val order = Order()

  val o1 = OrderWithItem(order, Index(2))
  val o2 = OrderWithItem(o1, Index(3), 2)

  val placed = PlaceOrder(587, o2, LocalDateTime.now())

  placed match {
    case Successful(message) =>
      SMSSend(message)
    case other =>
      println(s"failed with: $other")
  }

}
