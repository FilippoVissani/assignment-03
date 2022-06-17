package pcd.assignment03.distributed_programming.model

trait Pluviometer:
  def id: Int
  def zoneId: Int
  def position: Point2D

object Pluviometer:

  def apply(id: Int, zoneId: Int, position: Point2D): Pluviometer =
    PluviometerImpl(id, zoneId, position)

  private case class PluviometerImpl(override val id: Int,
                                     override val zoneId: Int,
                                     override val position: Point2D) extends Pluviometer