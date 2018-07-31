package takeaway

import org.scalatest.FlatSpec

class SeeDishesTest extends FlatSpec {

  import org.scalatest.Matchers._

  "seeDishes" should "show a nice menu" in {
    val expected = "1: chicken - £1.99\n2: vegetable - £1.49\n3: fish - £2.19"
    val actual = SeeDishes()
    actual should be(expected)
  }

}
