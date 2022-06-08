package pcd.assignment03.actor_programming.entity

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

  /**
   * @param v vector to sum so the Point2D
   * @return
   */
  def sum(v: Vector2D): Point2D

object Point2D:
  def apply(x: Double, y: Double): Point2D = Point2DImpl(x, y)

   private case class Point2DImpl(
                                 override val x: Double,
                                 override val y: Double
                                 ) extends Point2D:
     override def sum(v: Vector2D): Point2D = Point2D(x + v.x, y + v.y)
