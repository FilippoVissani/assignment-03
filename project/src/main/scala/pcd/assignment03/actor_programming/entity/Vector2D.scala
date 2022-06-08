package pcd.assignment03.actor_programming.entity

import pcd.assignment03.actor_programming.entity.exception.NullVectorException

trait Vector2D:
  /**
   * x of the Vector2D
   */
  def x: Double

  /**
   * y of the Vector2D
   */
  def y: Double

  /**
   * @param k scalar by which the vector must be multiplied
   * @return a new Vector2D
   */
  def multiplyByScalar(k: Double): Vector2D

  /**
   * @param v vector to sum
   * @return new Vector2D
   */
  def sum(v: Vector2D): Vector2D

  /**
   * normalized Vector2D starting from the current
   */
  def normalize: Vector2D

object Vector2D:
  def apply(x: Double, y: Double): Vector2D = Vector2DImpl(x, y)

  def apply(from: Point2D, to: Point2D): Vector2D = Vector2DImpl(to.x - from.x, to.y - from.y)

  private case class Vector2DImpl(
                                   override val x: Double,
                                   override val y: Double
                                 ) extends Vector2D:

    override def multiplyByScalar(k: Double): Vector2D =
      Vector2D(x * k, y * k)

    override def normalize: Vector2D =
      val mod = Math.sqrt(x * x + y * y)
      if mod > 0 then Vector2D(x / mod, y / mod)
      else throw NullVectorException()

    override def sum(v: Vector2D): Vector2D =
      Vector2D(x + v.x, y + v.y)
