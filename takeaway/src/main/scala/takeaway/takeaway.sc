import takeaway.Takeaway._
import takeaway.Model._
import java.time._

menu

seeDishes

val order = Order()

val o1 = withItem(order, 2)
val o2 = withItem(o1, 3, 2)

placeOrder(o2, LocalDateTime.now())


