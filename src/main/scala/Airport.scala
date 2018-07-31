import scala.util.Random

case class Airport(planes: Set[Plane], name: String, capacity: Int)

object Airport {

  def apply(name: String): Airport = Airport(Set(), s"Airport called: $name", 100)

}

case class Plane(name: String)

sealed trait Weather
case object Sunny extends Weather
case object Stormy extends Weather

//


object Weather {

  def apply(seed: Int): Weather =
    if (seed == 0)
      Stormy
    else
      Sunny

}

object AirTrafficController {

  def land(airport: Airport, plane: Plane, weather: Weather): Airport =
    if (airport.planes.contains(plane)) {
      println(s"** couldn't land  ${plane.name} in ${airport.name} - already here")
      airport
    } else if (airport.planes.size >= airport.capacity) {
      println(s"** couldn't land  ${plane.name} in ${airport.name} - no space")
      airport
    } else if (weather == Stormy) {
      println(s"** couldn't land  ${plane.name} in ${airport.name} - stormy")
      airport
    } else {
      println(s"landed ${plane.name} in ${airport.name}")
      airport.copy(airport.planes.+(plane))
    }

  def takeOff(airport: Airport, plane: Plane, weather: Weather): Airport =
    if (!airport.planes.contains(plane)) {
      println(s"** couldn't take off ${plane.name} in ${airport.name} - not here")
      airport
    } else if (weather == Stormy) {
      println(s"** couldn't take off ${plane.name} in ${airport.name} - stormy")
      airport
    } else {
      println(s"taken off ${plane.name} in ${airport.name}")
      airport.copy(airport.planes.-(plane))
    }

}

object SystemDesigner {

  def newAirport(name: String, capacity: Int) =
    Airport.apply( name)

}

// with effects, not testable
object Main extends App {

  val ap1 = SystemDesigner.newAirport("AP1", 20)
  val ap2 = SystemDesigner.newAirport("ap2", 10)
  val planes = Range(1,50).map(no => Plane(s"plane$no"))
  val next = AirTrafficController.land(ap2, planes(0), randWeather())
  planes.tail.foldLeft(next){
    case (ap, plane) => AirTrafficController.land(ap, plane, randWeather())
  }

  def randWeather() =
    Weather(Random.nextInt(4))

}