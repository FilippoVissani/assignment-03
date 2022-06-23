package pcd.assignment03.distributed_programming.model

/**
 * This trait represents a 2D point
 */
trait Point2D:
  /**
   * X of the Point2D
   */
  def x: Int

  /**
   * Y of the Point2D
   */
  def y: Int

object Point2D:
  def apply(x: Int, y: Int): Point2D = Point2DImpl(x, y)

  private case class Point2DImpl(
                                  override val x: Int,
                                  override val y: Int
                                ) extends Point2D

