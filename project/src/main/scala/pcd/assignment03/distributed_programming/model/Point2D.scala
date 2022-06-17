package pcd.assignment03.distributed_programming.model

/**
 * This trait represents a 2D point
 */
trait Point2D:
  /**
   * X of the Point2D
   */
  def x: Double

  /**
   * Y of the Point2D
   */
  def y: Double

object Point2D:
  def apply(x: Double, y: Double): Point2D = Point2DImpl(x, y)

  private case class Point2DImpl(
                                  override val x: Double,
                                  override val y: Double
                                ) extends Point2D

