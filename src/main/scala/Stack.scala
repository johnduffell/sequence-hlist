object Stack extends App {

  def func(i: Int): Int =
    if (i == 0)
      0
    else if (i == 6)
      throw new RuntimeException("haha")
    else
      i + f2(i - 1)

  def f2(i: Int): Int = func(i)

  println(s"*****\n\nresult is ${func(10)}\n")

}
