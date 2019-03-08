package pointfree

import cats.data.{Kleisli, State}
import cats.{FlatMap, Id}
import pointfree.Categ.-->
import shapeless.{HList, HNil, Poly2}

object Categ {

  // invariant functions allow better type inference
  case class -->[IN, OUT](f: IN => OUT) {
    def apply(in: IN): OUT =
      f.apply(in)
    def compose[A](g: A --> IN): A --> OUT = { -->(x => f.apply(g.f(x))) }
    def andThen[A](g: OUT --> A): IN --> A = { -->(x => g.f(f.apply(x))) }

  }

  def andThen[IN, OUT, A](f: IN --> OUT, g: OUT --> A): IN --> A = { -->(x => g.f(f.f.apply(x))) }

  def id[A] = -->(identity[A])
  def diagonalMorphism[A]: A --> (A, A) = -->(a => (a, a))
  def leftProjection[A, B]: (A, B) --> A = -->(ab => ab._1)
  def productBifunctor[IN1, IN2, RES1, RES2](f1: IN1 --> RES1, f2: IN2 --> RES2): -->[(IN1, IN2), (RES1, RES2)] =
    -->(in => (f1.f(in._1), f2.f(in._2)))
  def toRightAssoc[A, B, C]: -->[((A, B), C), (A, (B, C))] =
    --> { case ((a, b), c) => (a, (b, c)) }

}

object ComposeHList2 extends App {

  import Categ._

  val unitString: Unit --> String = --> { _: Unit =>
    println("unitString")
    "unitString"
  }
  val unitUnit: Unit --> Unit = --> { _: Unit =>
    println("unitUnit")
  }
  val stringUnit: String --> Unit = --> { string =>
    println(s"stringUnit: $string")
  }

  val com = diagonalMorphism[Unit]
    .andThen(
    productBifunctor(unitString, id)
  ).andThen(
    productBifunctor(id, unitUnit)
  ).andThen(
    leftProjection
  ).andThen(
    stringUnit
  )

  val com1 = stringUnit
    .compose[(String, Unit)](
    // with compose, only need type params where you lose information.
    // With andThen it would be the other way round - when you gain info.
      leftProjection
    ).compose(
      productBifunctor(id, unitUnit)
    ).compose(
      productBifunctor(unitString, id)
    ).compose(
      diagonalMorphism
    )

  println(s"com: $com ${com()}")

}

object ComposeHList extends App {

  import Categ._

  val f: (String, Int) --> Long = --> {
    case (string, int) =>
      println(s"f($string, $int) = 12L")
      12L
  }
  val g: (String, Long) --> Char = --> {
    case (string, long) =>
      println(s"g($string, $long) = 'a'")
      'a'
  }


  val com1 =
    productBifunctor(diagonalMorphism[String], id[Int])
      .andThen(
        toRightAssoc
      ).andThen(
      productBifunctor(id, f)
    ).andThen(g)

  val com = g
    .compose(
      productBifunctor(id, f)
    ).compose(
      toRightAssoc
    ).compose(
      productBifunctor(diagonalMorphism, id)
    )

  println(s"com: $com ${com(("hello", 23))}")

}
class CheckoutStep[M[_]] {

  implicit class KleisliStateOps[A, B](k1: A => B) {

    def compose2[Z](k: Z => A): Z => B =
      k1 compose k

    def composeL[Z, A2](k: Z => A2): ((Z, A)) => B =
      ???

  }

  type EnterEmail[EMAIL] = Unit => EMAIL // types are how we prove that our program makes sense
  type TakePayment = Unit => Unit // these types provide only just enough essential information to guarantee the ordering
  type SendEmail[EMAIL] = EMAIL => Unit // any accidental complexity would be in the abstract type

  def default[EMAIL](
    enterEmail: EnterEmail[EMAIL],// parameters are how we abstract a function over different inputs
    takePayment: TakePayment,
    sendEmail: SendEmail[EMAIL]
  )(implicit ev: FlatMap[M]): Unit => Unit = { //flatmap means we can put operations in an order
    val wrapped = {email: EMAIL =>
      takePayment.andThen(_ => email)()
    }
    sendEmail.compose2(wrapped).compose2(enterEmail)
//    sendEmail.composeL(takePayment).composeL(enterEmail)
  }

}
object CheckoutStep {

  val checkoutStep = new CheckoutStep[Id]
  val enterEmail: checkoutStep.EnterEmail[String] = { _ =>
    println("enter email please")
    "john@gu.com"
  }
  val takePayment: checkoutStep.TakePayment = { _ =>
    println("taking payment")
  }
  val sendEmail: checkoutStep.SendEmail[String] = { email =>
    println(s"sending email to: $email")
  }
  val default = checkoutStep.default(enterEmail, takePayment, sendEmail)
  println(s"finished with $default")


}
