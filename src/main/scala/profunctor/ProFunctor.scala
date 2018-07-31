package profunctor

import scala.language.higherKinds

trait CovariantFunctor[PRODUCER_CATEGORY[_]] {

  def associateArrow[OUT, NEWOUT](
    arrow: OUT => NEWOUT
  ): PRODUCER_CATEGORY[OUT] => PRODUCER_CATEGORY[NEWOUT]

}

object CoTesting {

  case class ProducerCategory[OUT](produce: Unit => OUT)

  implicit val valueFunctor: CovariantFunctor[ProducerCategory] =
    new CovariantFunctor[ProducerCategory] {

      override def associateArrow[OUT, NEWOUT](arrow: OUT => NEWOUT): ProducerCategory[OUT] => ProducerCategory[NEWOUT] = {
        case ProducerCategory(oldProducer) =>
          ProducerCategory(_ => arrow(oldProducer()))
      }

    }

}

trait ContravariantFunctor[CONSUMER_CATEGORY[_]] {

  def associateArrow[NEWIN, IN](
    arrow: NEWIN => IN
  ): CONSUMER_CATEGORY[IN] => CONSUMER_CATEGORY[NEWIN]

}

object ConTesting {

  case class ConsumerCategory[IN](consume: IN => Unit)

  implicit val valueFunctor: ContravariantFunctor[ConsumerCategory] =
    new ContravariantFunctor[ConsumerCategory] {

      override def associateArrow[NEWIN, IN](arrow: NEWIN => IN): ConsumerCategory[IN] => ConsumerCategory[NEWIN] = {
        case ConsumerCategory(oldConsumer) =>
          ConsumerCategory(newIn => oldConsumer(arrow(newIn)))
      }

    }

}

trait ProFunctor[PRODUCER_CONSUMER_CATEGORY[_, _]] {

  def associateArrow[IN, OUT, NEWIN, NEWOUT](
    contraArrow: NEWIN => IN,
    coArrow: OUT => NEWOUT
  ): PRODUCER_CONSUMER_CATEGORY[IN, OUT] => PRODUCER_CONSUMER_CATEGORY[NEWIN, NEWOUT]

}

object Testing {

  case class ProducerConsumerCategory[IN, OUT](f: IN => OUT)

  implicit val func: ProFunctor[ProducerConsumerCategory] =
    new ProFunctor[ProducerConsumerCategory] {

      override def associateArrow[IN, OUT, NEWIN, NEWOUT](
        contraArrow: NEWIN => IN,
        coArrow: OUT => NEWOUT
      ): ProducerConsumerCategory[IN, OUT] => ProducerConsumerCategory[NEWIN, NEWOUT] = {
        case ProducerConsumerCategory(oldProducerConsumer) =>
          ProducerConsumerCategory((newIn: NEWIN) => coArrow(oldProducerConsumer(contraArrow(newIn))))
      }

    }


}