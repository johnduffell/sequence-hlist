package profunctor

object HttpMonoid {

  case class Request(value: String)
  case class Response(value: String)

  case class HTTPOperation[IN, OUT](
    createFirstRequest: IN => Request,
    createNextRequestsFromPrevious: List[Response => Request],
    processFinalResponse: Response => OUT
  ) {
    def andThen[NEWOUT](that: HTTPOperation[OUT, NEWOUT]): HTTPOperation[IN, NEWOUT] = {
      val extraMid = processFinalResponse andThen that.createFirstRequest
      val newMids = createNextRequestsFromPrevious ++ (extraMid :: that.createNextRequestsFromPrevious)
      HTTPOperation(createFirstRequest, newMids, that.processFinalResponse)
    }

    def dimap[NEWIN, NEWOUT](outerPre: NEWIN => IN, outerPost: OUT => NEWOUT): HTTPOperation[NEWIN, NEWOUT] =
      HTTPOperation(outerPre andThen createFirstRequest, List(), processFinalResponse andThen outerPost)

    def map[NEWOUT]( outerPost: OUT => NEWOUT): HTTPOperation[IN, NEWOUT] =
      HTTPOperation(createFirstRequest, List(), processFinalResponse andThen outerPost)

    def contramap[NEWIN](outerPre: NEWIN => IN): HTTPOperation[NEWIN, OUT] =
      HTTPOperation(outerPre andThen createFirstRequest, List(), processFinalResponse)

  }

  val init: HTTPOperation[Request, Response] = HTTPOperation(identity, List(), identity)

  def intepret[IN, OUT](getResponse: Request => Response)(withHttp: HTTPOperation[IN, OUT]): IN => OUT = {
    val middle = withHttp.createNextRequestsFromPrevious.foldLeft(getResponse) {
      case (sofar, mid) => sofar andThen mid andThen getResponse
    }
    withHttp.createFirstRequest andThen middle andThen withHttp.processFinalResponse
  }

}

object HttpMonad {

  case class Request(value: String)
  case class Response(value: String)

  case class HTTPOperation[IN, OUT](
    createFirstRequest: IN => Request,
    createNextRequestsFromPrevious: List[Response => Option[Request]],
    processFinalResponse: Response => Option[OUT]
  ) {
    def andThen[NEWOUT](that: HTTPOperation[OUT, NEWOUT]): HTTPOperation[IN, NEWOUT] = {
      val extraMid = processFinalResponse andThen (_.map(that.createFirstRequest))
      val newMids = createNextRequestsFromPrevious ++ (extraMid :: that.createNextRequestsFromPrevious)
      HTTPOperation(createFirstRequest, newMids, that.processFinalResponse)
    }

    def dimap[NEWIN, NEWOUT](outerPre: NEWIN => IN, outerPost: OUT => NEWOUT): HTTPOperation[NEWIN, NEWOUT] =
      HTTPOperation(outerPre andThen createFirstRequest, List(), processFinalResponse andThen(_.map(outerPost)))

    def contramap[NEWIN](outerPre: NEWIN => IN): HTTPOperation[NEWIN, OUT] =
      HTTPOperation(outerPre andThen createFirstRequest, List(), processFinalResponse)

    def map[NEWOUT](outerPost: OUT => NEWOUT): HTTPOperation[IN, NEWOUT] =
      HTTPOperation(createFirstRequest, List(), processFinalResponse andThen(_.map(outerPost)))

    def optMap[NEWOUT](outerPost: OUT => Option[NEWOUT]): HTTPOperation[IN, NEWOUT] =
      HTTPOperation(createFirstRequest, List(), processFinalResponse andThen(_.flatMap(outerPost)))

  }

  val init: HTTPOperation[Request, Response] = HTTPOperation(identity, List(), Some.apply)

  def intepret[IN, OUT](getResponse: Request => Response)(withHttp: HTTPOperation[IN, OUT]): IN => Option[OUT] = {
    val middle = withHttp.createNextRequestsFromPrevious.foldLeft[Request => Option[Response]](getResponse andThen Some.apply) {
      case (sofar, mid) => sofar andThen(_.flatMap(mid)) andThen(_.map(getResponse))
    }
    withHttp.createFirstRequest andThen middle andThen(_.flatMap(withHttp.processFinalResponse))
  }

}

object TestHttp extends App {

  import HttpMonad._

  //effects
  def getResponse(request: Request): Response = {
    println(s"    >>>DID HTTP!! $request")
    Response(request.value)
  }

  object LowLevel {
    def bodyToHttp(body: List[Char]): Request =
      Request(s"(LOWLEVEL PRE $body)")
    def responseToBody(response: Response): List[Char] =
      s"(LOWLEVEL POST ${response.toString})".toCharArray.toList
  }

  object HighLevelIntToStr {
    def inputToBody(input: Int): List[Char] =
      s"input: $input".toCharArray.toList
    def bodyToInt(response: List[Char]): String =
      new String(response.toArray)
  }

  case class Wrap(value: String)
  object HighLevelStrToWrap {
    def inputToBody(input: String): List[Char] =
      s"input: $input".toCharArray.toList
    def bodyToInt(response: List[Char]): Wrap =
      Wrap(new String(response.toArray))
  }

  {
    val initRes = intepret(getResponse)(init)(Request("hello"))

    println(s"initRes: $initRes")
  }
  val lowLevel = init.dimap(LowLevel.bodyToHttp, LowLevel.responseToBody)

  {
    val lowLevelRes = intepret(getResponse)(lowLevel)(List('h', 'i'))

    println(s"lowLevelRes: $lowLevelRes")
  }
  val highLevelIntToStr = lowLevel.dimap(HighLevelIntToStr.inputToBody, HighLevelIntToStr.bodyToInt)

  {
    val highLevelIntToStrRes = intepret(getResponse)(highLevelIntToStr)(123)

    println(s"highLevelIntToStr: $highLevelIntToStrRes")
  }
  val highLevelStrToWrap = lowLevel.dimap(HighLevelStrToWrap.inputToBody, HighLevelStrToWrap.bodyToInt)

  {
    val highLevelStrToWrapRes = intepret(getResponse)(highLevelStrToWrap)("updated")

    println(s"highLevelStrToWrap: $highLevelStrToWrapRes")
  }

  val flatMapped = highLevelIntToStr.andThen(highLevelStrToWrap)

  {
    val flatMappedRes = intepret(getResponse)(flatMapped)(123)

    println(s"flatMapped: $flatMappedRes")
  }

}