package pcd.assignment03.distributed_programming.model

trait Pluviometer:
  def zoneId: Int
  def position: Point2D
  def threshold: Float

object Pluviometer:

  def apply(zoneId: Int, position: Point2D, threshold: Float): Pluviometer =
    PluviometerImpl(zoneId, position, threshold)

  private case class PluviometerImpl(override val zoneId: Int,
                                     override val position: Point2D,
                                     override val threshold: Float) extends Pluviometer
  