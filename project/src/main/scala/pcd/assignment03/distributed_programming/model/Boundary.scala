package pcd.assignment03.distributed_programming.model

/**
 * Boundary of the field where bodies move.
 */
trait Boundary:
  /**
   * left bound
   */
  def x0: Int
  /**
   * top bound
   */
  def y0: Int
  /**
   * right bound
   */
  def x1: Int
  /**
   * bottom bound
   */
  def y1: Int
  
  def width: Int = x1 - x0
  
  def height: Int = y1 - y0

object Boundary:
  def apply(x0: Int, y0: Int, x1: Int, y1: Int): Boundary = BoundaryImpl(x0, y0, x1, y1)

  private case class BoundaryImpl(
                                   override val x0: Int,
                                   override val y0: Int,
                                   override val x1: Int,
                                   override val y1: Int
                                 ) extends Boundary
