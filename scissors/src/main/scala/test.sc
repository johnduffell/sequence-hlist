import scala.util.Random

val infinite1 = Stream.continually(1)
val infinite2 = Stream.continually(2)

val both = infinite1.zip(infinite2)
both.take(5).toList

both

lazy val f: Stream[Int] = 0 #:: 1 #:: f.zip(f.tail).map { case (first, second) => first + second }
f.take(10).toList

val stRand = Stream.continually(Random.nextInt(3))

List(1,2,0).toStream

val hum = Stream.continually({println("what is your move?"); readLine()})