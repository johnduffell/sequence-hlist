
val danger = "YIKES!"
val secret = "hui840"

val autoInterpolated = s"sensitive problem occurred $danger, id: $secret"

val manualInterpolated =
  StringContext(
    "sensitive problem occurred ", ", id: ", ""
  ).s(
    danger, secret
  )

case class Scrubbed(danger: String, safe: String) {
  override val toString = safe
}

implicit class Scrubber(val sc: StringContext) extends AnyVal {

  def scrub(args: Any*): Scrubbed = {
    Scrubbed(
      sc.s(args: _*),
      sc.s(args.map(_ => "*****"): _*)
    )
  }

}


val context = StringContext("An interesting string with ", " stuff").s(secret)

val scrubbed = scrub"sensitive problem occurred $danger, id: $secret"

val safeLogLine = scrubbed.safe
val secretLogLine = scrubbed.danger
