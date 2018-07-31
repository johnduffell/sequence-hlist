package takeaway

import java.time.LocalDateTime

import org.scalatest.FlatSpec
import takeaway.Model._

class OrderWithItemTest extends FlatSpec {

  import org.scalatest.Matchers._

  "orderWithItem" should "add to empty" in {
    val expected = Order(Map(Index(2) -> 3))
    val order = Order()
    val actual = OrderWithItem(order, Index(2), 3)
    actual should be(expected)
  }

  "orderWithItem" should "have 1 as the default value" in {
    val expected = Order(Map(Index(2) -> 1))
    val order = Order()
    val actual = OrderWithItem(order, Index(2))
    actual should be(expected)
  }

  "orderWithItem" should "add new item to existing different" in {
    val expected = Order(Map(
      Index(2) -> 3,
      Index(1) -> 4)
    )
    val order = Order(Map(Index(1) -> 4))
    val actual = OrderWithItem(order, Index(2), 3)
    actual should be(expected)
  }

  "orderWithItem" should "add to existing total when the same" in {
    val expected = Order(Map(Index(2) -> 7))
    val order = Order(Map(Index(2) -> 4))
    val actual = OrderWithItem(order, Index(2), 3)
    actual should be(expected)
  }

}
