package pcd.assignment03.actor_programming.util

trait Range [A, B]:
  def a: A
  def b: B
  
object Range:
  def apply[A, B](a: A, b: B): Range[A, B] = RangeImpl[A, B](a, b)
  
  private case class RangeImpl[A, B](
                                override val a: A,
                                override val b: B
                              )extends Range[A, B]
