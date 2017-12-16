# sequence-hlist
Just a quick demo that we can keep type safety when performing a functor operation (mapping) an arbitrary number of an applicative with different type parameter

EG we have Future[String] Future[Int] and Future[Boolean]
we want to wait for them all and use the result in a type safe way without intorucing any dependency between them (ie don't flatmap)

For this we have 3 choices
- use zip which is built in but a bit basic and needs tidyup code
- use applicative from cats which is neat but needs odd imports
- use hlist which requires the ability to sequence an hlist

Personally I prefer the applicative, but it is limited to only 12 or so.