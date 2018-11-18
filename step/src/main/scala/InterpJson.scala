import play.api.libs.json.{Json, OFormat, Reads, Writes}

trait InterpJson[STEP <: StepsAlg[_]] {
  type FINAL
  def run(step: STEP): (List[String => Option[String]], String => Option[FINAL])
}
object InterpJson {

  def apply[FROM: Writes, REST <: StepsAlg[FROM]](
    value: FROM,
    steps: REST
  )(implicit canRun: InterpJson[REST]) = {
    val (runs, finalReads) = canRun.run(steps)

    val fromString: String = Json.stringify(Json.toJson(value))
    val finalString = runs.foldLeft[Option[String]](Some(fromString)){ case (str, nextStep) =>
      str.flatMap(nextStep)
    }
    finalString.flatMap(finalReads)

  }

  implicit def canRunEndStepJson[FROM: Reads, FINAL1: OFormat]: InterpJson[EndStep[FROM, FINAL1]] =
    new InterpJson[EndStep[FROM, FINAL1]] {
      override type FINAL = FINAL1

      override def run(step: EndStep[FROM, FINAL1]): (List[String => Option[String]], String => Option[FINAL1]) = {
        val fn: String => Option[String] = { fromRaw: String =>
          Json.parse(fromRaw).validate[FROM].asOpt.map { from =>
            val final1 = step.lambda(from)
            Json.stringify(Json.toJson(final1))
          }

        }
        (List(fn), {s: String => Json.parse(s).validate[FINAL].asOpt})
      }

    }

  implicit def canRunTaskStepJson[FROM: Reads, TO: Writes, REST <: StepsAlg[TO]](
    implicit canRunRest: InterpJson[REST]
  ): InterpJson[TaskStep[FROM, TO, REST]] =
    new InterpJson[TaskStep[FROM, TO, REST]] {
      override type FINAL = canRunRest.FINAL

      override def run(step: TaskStep[FROM, TO, REST]): (List[String => Option[String]], String => Option[FINAL]) = {
        val fn: String => Option[String] = { fromRaw: String =>
          Json.parse(fromRaw).validate[FROM].asOpt.map { from =>
            val final1 = step.lambda(from)
            Json.stringify(Json.toJson(final1))
          }

        }
        val (restFunctions, readFinal) = canRunRest.run(step.rest)
        (fn :: restFunctions, readFinal)
      }

    }

}