package takeaway

import java.time.LocalDateTime

import org.scalatest.FlatSpec
import takeaway.Model._

class PlaceOrderTest extends FlatSpec {

  val now = LocalDateTime.of(2018, 3, 3, 11, 38)

  import org.scalatest.Matchers._

  "order" should "calculate a total and send a text" in {
    val expected = Successful("your order will arrive at some stage: 12:38")
    val order = Order(Map(
      Index(1) -> 2
    ))
    val actual = PlaceOrder(398, order, now)
    actual should be(expected)
  }

  "order" should "calculate a total and send a text invalid index" in {
    val expected = InvalidOrder
    val order = Order(Map(
      Index(-1) -> 2
    ))
    val actual = PlaceOrder(0, order, now)
    actual should be(expected)
  }

  "order" should "calculate a total and send a text negative quantity" in {
    val expected = InvalidOrder
    val order = Order(Map(
      Index(1) -> -2
    ))
    val actual = PlaceOrder(-398, order, now)
    actual should be(expected)
  }

  "order" should "complain if you don't pay correctly" in {
    val expected = WrongPaymentAmount
    val order = Order(Map(
      Index(1) -> 2
    ))
    val actual = PlaceOrder(12, order, now)
    actual should be(expected)
  }

}
