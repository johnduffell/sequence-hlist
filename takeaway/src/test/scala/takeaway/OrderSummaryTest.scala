package takeaway

import java.time.LocalDateTime

import org.scalatest.FlatSpec
import takeaway.Model._

class OrderSummaryTest extends FlatSpec {

  val now = LocalDateTime.of(2018, 3, 3, 11, 38)

  import org.scalatest.Matchers._

  "order" should "calculate a total and send a text" in {
    val expected = "chicken x2 = £3.98"
    val order = Order(Map(
      Index(1) -> 2
    ))
    val actual = OrderSummary(order)
    actual should be(expected)
  }

}
