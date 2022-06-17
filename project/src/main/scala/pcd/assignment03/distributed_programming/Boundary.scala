package pcd.assignment03.distributed_programming

/**
 * Boundary of the field where bodies move.
 */
trait Boundary:
  /**
   * left bound
   */
  def x0: Double
  /**
   * top bound
   */
  def y0: Double
  /**
   * right bound
   */
  def x1: Double
  /**
   * bottom bound
   */
  def y1: Double
  
  def width: Double = x1 - x0
  
  def height: Double = y1 - y0

object Boundary:
  def apply(x0: Double, y0: Double, x1: Double, y1: Double): Boundary = BoundaryImpl(x0, y0, x1, y1)

  private case class BoundaryImpl(
                                   override val x0: Double,
                                   override val y0: Double,
                                   override val x1: Double,
                                   override val y1: Double
                                 ) extends Boundary
