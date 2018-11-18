import play.api.libs.json.{Json, Reads, Writes}

trait InterpJsonRaw[STEP <: StepsAlg[_]] {
  def run(step: STEP): List[String => Option[String]]
}
object InterpJsonRaw {

  def apply[REST <: StepsAlg[_]](steps: REST)(implicit canRun: InterpJsonRaw[REST]) = {
    val runs = canRun.run(steps)

    { value: String =>
      runs.foldLeft[Option[String]](Some(value)){ case (str, nextStep) =>
        str.flatMap(nextStep)
      }
    }
  }

  implicit def canRunEndStepJson[FROM: Reads, FINAL: Writes]: InterpJsonRaw[EndStep[FROM, FINAL]] =
    new InterpJsonRaw[EndStep[FROM, FINAL]] {

      override def run(step: EndStep[FROM, FINAL]): List[String => Option[String]] = {
        val fn: String => Option[String] = { fromRaw: String =>
          Json.parse(fromRaw).validate[FROM].asOpt.map { from =>
            val final1 = step.lambda(from)
            Json.stringify(Json.toJson(final1))
          }

        }
        List(fn)
      }

    }

  implicit def canRunTaskStepJson[FROM: Reads, TO: Writes, REST <: StepsAlg[TO]](
    implicit canRunRest: InterpJsonRaw[REST]
  ): InterpJsonRaw[TaskStep[FROM, TO, REST]] =
    new InterpJsonRaw[TaskStep[FROM, TO, REST]] {

      override def run(step: TaskStep[FROM, TO, REST]): List[String => Option[String]] = {
        val fn: String => Option[String] = { fromRaw: String =>
          Json.parse(fromRaw).validate[FROM].asOpt.map { from =>
            val final1 = step.lambda(from)
            Json.stringify(Json.toJson(final1))
          }

        }
        fn :: canRunRest.run(step.rest)
      }

    }

}
