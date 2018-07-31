import scala.annotation.tailrec

@tailrec
def foldLeft[ACC, EL](list: List[EL], z: ACC)(f: (ACC, EL) => ACC): ACC = {
  list match {
    case Nil => z
    case head :: tail =>
      foldLeft(tail, f(z, head))(f)
  }
}


def foldRight[ACC, EL](list: List[EL], z: ACC)(f: (EL, ACC) => ACC): ACC = {
  list match {
    case Nil => z
    case head :: tail =>
      f(head, foldRight(tail, z)(f))
  }
}

def foldRightT[ACC, EL](list: List[EL], z: ACC)(f: (EL, ACC) => ACC): ACC = {
  val f2 = foldLeft(
    list.map(el => (acc: ACC) => f(el, acc)),
    identity[ACC] _
  )({ case (acc, el) => x: ACC => acc(el(x))})
  f2(z)
}

// . 1 2 3
foldLeft(List(1,2,3), "."){case (acc, el) => s"$acc $el"}

// 1 2 3 .
foldRight(List(1,2,3), "."){case (el, acc) => s"$el $acc"}

// 1 2 3 .
foldRightT(List(1,2,3), "."){case (el, acc) => s"$el $acc"}
