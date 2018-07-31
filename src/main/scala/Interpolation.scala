//
//object John {
//
//  case class Scrubbed(danger: String, safe: String) {
//    override val toString = safe
//  }
//
//  implicit class Scrubber(val sc: StringContext) extends AnyVal {
//
//    def scrub(args: InsertableString*): Scrubbed = {
//      Scrubbed(
//        sc.s(args: _*),
//        sc.s(args.map(_ => "*****"): _*)
//      )
//    }
//
//  }
//
//  sealed trait InsertableString
//  case object Secret extends InsertableString
////  case class NonSecret(value: String) extends AnyVal with InsertableString
//
//  case class Username(value: String) extends AnyVal
//
//  case class Password(value: String) extends AnyVal
//
//  object Main extends App {
//
//    val username = Username("john")
//    val password = Password("secret")
//
//    //println(scrub"user $username password $password")
//
//    }
//
//}
//
//object Guy {
//
//  import java.lang.StringBuilder
//
//  trait LoggingArg {
//    def value: String
//  }
//
//  trait Scrub {
//
//    protected implicit class ScrubOps(sc: StringContext) {
//      def scrub(args: LoggingArg*): String = {
//        val pi = sc.parts.iterator
//        val ai = args.iterator
//        val builder = new StringBuilder(pi.next())
//        while (ai.hasNext) {
//          builder.append(ai.next().value)
//          builder.append(pi.next())
//        }
//        builder.toString
//      }
//    }
//
//    protected implicit def loggingParameter[A: ToLoggingArg](a: A): LoggingArg =
//      implicitly[ToLoggingArg[A]].toLoggingParameter(a)
//  }
//
//  trait ToLoggingArg[A] {
//    def toLoggingParameter(a: A): LoggingArg
//  }
//
//  object ToLoggingArg {
//    def instance[A](f: A => String): ToLoggingArg[A] = (a: A) => new LoggingArg {
//      override val value: String = f(a)
//    }
//  }
//
//  trait SensitiveData[A] {
//    implicit val sensitive: ToLoggingArg[A] = ToLoggingArg.instance(_ => "*****")
//  }
//
//  trait NonSensitiveData[A <: AnyVal] {
//    def unapply(a: A): Option[String]
//
//    implicit val nonSensitive: ToLoggingArg[A] = ToLoggingArg.instance(unapply(_).getOrElse("unknown"))
//  }
//
//  case class Username(value: String) extends AnyVal
//
//  object Username extends NonSensitiveData[Username]
//
//  case class Password(value: String) extends AnyVal
//
//  object Password extends SensitiveData[Password]
//
//  case class SecretAnswer(value: String) extends AnyVal
//
//  object SecretAnswer extends SensitiveData[SecretAnswer]
//
//  case class LoginDetails(username: Username, password: Password, secretAnswer: SecretAnswer)
//
//  object Main extends App with Scrub {
//
//    val login = LoginDetails(
//      username = Username("guy"),
//      password = Password("secret"),
//      secretAnswer = SecretAnswer("otis")
//    )
//
//    println(scrub"user ${login.username} forgot password ${login.password} but provided secret answer ${login.secretAnswer}")
//  }
//
//}