import cats.instances.unit
import cats.{FlatMap, Id}
import cats.syntax.flatMap._
import cats.syntax.functor._

import scala.concurrent.Future

case class Amounts(amounts: List[Int], other: Boolean)
object Amounts {
  val default = Amounts(List(2, 5, 10), other = true)
}

sealed trait Currencies
object Currencies {

  case object GBP extends Currencies

  case object EUR extends Currencies

  case object USD extends Currencies

  val default = List(GBP, EUR, USD)

}


class CheckoutStep[M[_]] {

  type EnterEmail[EMAIL] = Unit => M[EMAIL] // types are how we prove that our program makes sense
  type TakePayment = Unit => M[Unit] // these types provide only just enough essential information to guarantee the ordering
  type SendEmail[EMAIL] = EMAIL => M[Unit] // any accidental complexity would be in the abstract type

  def default[EMAIL](
    enterEmail: EnterEmail[EMAIL],// parameters are how we abstract a function over different inputs
    takePayment: TakePayment,
    sendEmail: SendEmail[EMAIL]
  )(implicit ev: FlatMap[M]): M[Unit] = //flatmap means we can put operations in an order
    for {
      email <- enterEmail()
      _ <- takePayment()
      _ <- sendEmail(email)
    } yield ()

}
object CheckoutStep extends App {

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

trait NonAssocAlg[ELEM] {
  type AGG
  def op(head: ELEM, tail: AGG): AGG
  def nil: AGG

  object Syntax {

    implicit class Implicits(agg: AGG) {
      def :::(elem: ELEM): AGG = op(elem, agg)
    }

    def nonAssoc(elems: ELEM*): AGG =
      elems.toList.foldRight(nil)(op)
  }

}
class AssocProgram {
  def default(nonAssocAlg: NonAssocAlg[Int]): nonAssocAlg.AGG = {
    import nonAssocAlg.Syntax._
    2 ::: 5 ::: 10 ::: nonAssocAlg.nil
    nonAssoc(2, 5, 10)
  }
}
object AssocProgram extends App {
  val program = new AssocProgram
  val assocInterp = new NonAssocAlg[Int] {

    override type AGG = List[Int]

    override def op(head: Int, tail: List[Int]): List[Int] = {
      println(s"head: $head, tail: $tail")
      head :: tail
    }

    override def nil: List[Int] = {
      println("TAIL")
      Nil
    }
  }
  program.default(assocInterp)
}

//sealed trait Cond
//object Cond {
//
//  case object WithEmail extends Cond
//
//}
//
//sealed trait CheckoutStep[REQUIRES] {
//  type PROVIDES
//}
//object CheckoutStep {
//
//  case object EnterEmail extends CheckoutStep[Any] {
//    override type PROVIDES = WithEmail.type
//  }
//
//  case object TakePayment extends CheckoutStep[Any] {
//    override type PROVIDES = Nothing
//  }
//
//  case object SendEmail extends CheckoutStep[WithEmail.type] {
//    override type PROVIDES = Nothing
//  }
//
//}
//
//sealed trait OrderedExecution[FROM]
//
//// prepend a task
//case class EndStep[FROM1, TO](lambda: CheckoutStep[FROM1]{ type PROVIDES = TO}) extends OrderedExecution[FROM1]
//
//// prepend a task
//case class TaskStep[FROM1, TO, REST <: OrderedExecution[TO]](
//  lambda: CheckoutStep[FROM1] { type PROVIDES = TO},
//  rest: REST
//) extends OrderedExecution[FROM1]
//
//object CheckoutExecution {
//  val default = TaskStep(EnterEmail, TaskStep(TakePayment, EndStep(SendEmail)))
//}

/*
sealed trait OrderedExecution[FROM]
object OrderedExecution {
  case class FinalStep[REQUIRES, PROVIDES](checkoutStep: CheckoutStep[REQUIRES, PROVIDES]) extends OrderedExecution[REQUIRES]
  case class PreviousStep[REQUIRES, PROVIDES](
    checkoutStep: CheckoutStep[REQUIRES, PROVIDES],
    orderedExecution: OrderedExecution[PROVIDES]
  ) extends OrderedExecution[REQUIRES]
}

case class CheckoutProcess[APPENDABLE[_]: Appender](steps: APPENDABLE[OrderedExecution[Nothing]])
object CheckoutProcess {
  private val appender: Appender[OrderedExecution] = implicitly[Appender[OrderedExecution]]
  val default = CheckoutProcess(appender.append(appender.append(appender.wrap(EnterEmail), appender.wrap(TakePayment)), appender.wrap(SendEmail)))

  implicit val i: Appender[OrderedExecution] = new Appender[OrderedExecution] {

    override def wrap[REQUIRES, PROVIDES](checkoutStep: CheckoutStep[REQUIRES, PROVIDES]): OrderedExecution[REQUIRES] = FinalStep(checkoutStep)
    override def append[REQUIRESF, REQUIRESS](
      first: OrderedExecution[REQUIRESF],
      second: OrderedExecution[REQUIRESS]
    ): OrderedExecution[REQUIRESF] =
      (first, second) match {
        case (FinalStep(step), order) => PreviousStep(step, order)
        case (PreviousStep(head, rest), order) => PreviousStep(head, append(rest, order))
      }

  }

}

case class PreviousContributors(emails: List[String])
object PreviousContributors {
  val default = PreviousContributors(List())
}

trait Appender[APPENDABLE[_]] {
  def wrap[REQUIRES, PROVIDES](checkoutStep: CheckoutStep[REQUIRES, PROVIDES]): APPENDABLE[REQUIRES]
  def append[REQUIRESF, REQUIRESS](first: APPENDABLE[REQUIRESF], second: APPENDABLE[REQUIRESS]): APPENDABLE[REQUIRESF]
}
*/