package pointfree

import cats.{FlatMap, Id}
import cats.data.Kleisli


class CheckoutStep2[M[_]] {

  type EnterEmail[EMAIL] = Kleisli[M, Unit, EMAIL] // types are how we prove that our program makes sense
  type TakePayment = Kleisli[M, Unit, Unit] // these types provide only just enough essential information to guarantee the ordering
  type SendEmail[EMAIL] = Kleisli[M, EMAIL, Unit] // any accidental complexity would be in the abstract type

  def default[EMAIL](
    enterEmail: EnterEmail[EMAIL],// parameters are how we abstract a function over different inputs
    takePayment: TakePayment,
    sendEmail: SendEmail[EMAIL]
  )(implicit ev: FlatMap[M]): Kleisli[M, Unit, Unit] = { //flatmap means we can put operations in an order
    val wrapped = Kleisli[M, EMAIL, EMAIL]({email: EMAIL =>
      takePayment.map(_ => email).run()
    })
    enterEmail.andThen(wrapped).andThen(sendEmail)
  }

}
object CheckoutStep2 {

  val checkoutStep = new CheckoutStep2[Id]
  val enterEmail: checkoutStep.EnterEmail[String] = Kleisli[Id, Unit, String]{ _ =>
    println("enter email please")
    "john@gu.com"
  }
  val takePayment: checkoutStep.TakePayment = Kleisli[Id, Unit, Unit]{ _ =>
    println("taking payment")
  }
  val sendEmail: checkoutStep.SendEmail[String] = Kleisli[Id, String, Unit] { email =>
    println(s"sending email to: $email")
  }
  val default = checkoutStep.default(enterEmail, takePayment, sendEmail)
  println(s"finished with $default")


}
