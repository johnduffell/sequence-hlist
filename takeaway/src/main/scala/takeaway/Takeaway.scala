package takeaway

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import takeaway.Model.Order

object Model {

  case class MenuItem(dish: String, price: Price)

  case class Price(pence: Int)

  case class Menu(dishes: Map[Index, MenuItem])

  case class Index(value: Int)
  case class Quantity(value: Int)

  type OrderLines = Map[Index, Int]

  case class Order(lines: OrderLines = Map())


  trait OrderResult
  case class Successful(message: String) extends OrderResult
  case object InvalidOrder extends OrderResult
  case object WrongPaymentAmount extends OrderResult

}

import Model._

object SeeDishes {

  val menu: Menu = Menu(
    List(
      ("chicken", 199),
      ("vegetable", 149),
      ("fish", 219)
    ).zipWithIndex.map {
      case ((name, price), index) =>
        (Index(index + 1), MenuItem(name, Price(price)))
    }.toMap
  )

  def apply(): String =
    menu.dishes.map {
      case (Index(index), MenuItem(dish, Price(pence))) =>
        s"$index: $dish - £${pence / 100.0}"
    }.mkString("\n")
}

import SeeDishes._

object OrderWithItem {

  def apply(order: Order, item: Index, quantity: Int = 1): Order =
    Order(order.lines.updated((item), order.lines.getOrElse((item), 0) + quantity))

}

object OrderSummary {

  def apply(order: Order): String =
    order.lines.map {
      case (dishIndex, quantity) =>
        menu.dishes.get(dishIndex).map { item =>
          s"${item.dish} x${quantity} = £${(item.price.pence * quantity) / 100.0}"
        }.getOrElse("unknown item!")
    }.mkString(", ")

}

object PlaceOrder {

  def apply(payAmount: Int, order: Order, now: LocalDateTime): OrderResult = {
    total(order) match {
      case Some(Price(total)) if total == payAmount =>
        Successful(s"your order will arrive at some stage: ${renderArrivalTime(now)}")
      case Some(_) =>
        WrongPaymentAmount
      case None =>
        InvalidOrder
    }
  }

  def renderArrivalTime(now: LocalDateTime): String = {
    val when = now.plusHours(1)
    when.format(DateTimeFormatter.ofPattern("HH:mm"))
  }

  def total(order: Order): Option[Price] = {
    order.lines.map { case (dishIndex, quantity) =>
      menu.dishes.get(dishIndex).map(_.price.pence * quantity)
    }.foldLeft(Some(0): Option[Int]) {
      case (_, Some(next)) if next < 0 =>
        None
      case (Some(sofar), Some(next)) =>
        Some(sofar + next)
      case _ => None
    }.map(Price.apply)
  }
}

