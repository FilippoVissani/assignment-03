package pcd.assignment03.distributed_programming.model

trait Pluviometer:
  def id: String
  def zoneId: String
  def position: Point2D
  def threshold: Float

object Pluviometer:

  def apply(id: String, zoneId: String, position: Point2D, threshold: Float): Pluviometer =
    PluviometerImpl(id, zoneId, position, threshold)

  private case class PluviometerImpl(override val id: String,
                                     override val zoneId: String,
                                     override val position: Point2D,
                                     override val threshold: Float) extends Pluviometer
  