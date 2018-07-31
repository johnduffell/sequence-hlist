
 val fib: Stream[Int] =
   0 #::
     1 #::
     fib.zip(fib.tail).map {
       case (prev2, prev1) =>
         prev2 + prev1
     }


fib.take(10).toList
