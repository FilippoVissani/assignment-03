package pcd.assignment03.actor_programming.model

import pcd.assignment03.actor_programming.model.exception.InfiniteForceException

/**
 * Trait that represents a body in the simulation
 */
trait Body:
  /**
   * current position of the body
   */
  def position: Point2D

  /**
   * current speed of the body
   */
  def speed: Vector2D

  /**
   * mass of the body
   */
  def mass: Double

  /**
   * ID of the body
   */
  def id: Int

  /**
   * @param dt to be multiplied by speed and added to position
   */
  def updatePosition(dt: Double): Body

  /**
   * @param acceleration acceleration
   * @param dt           to be multiplied by acceleration and added to speed
   */
  def updateSpeed(acceleration: Vector2D, dt: Double): Body

  /**
   * @param body to calculate the distance from this body
   * @return the distance
   */
  def distanceFrom(body: Body): Double

  /**
   * @param body to calculate the repulsive force
   * @return the repulsive force under form of Vector2D
   */
  def computeRepulsiveForceBy(body: Body): Vector2D

  /**
   * current friction force
   */
  def currentFrictionForce: Vector2D

  /**
   * @param bounds used to check if a collision has occurred
   */
  def checkAndSolveBoundaryCollision(bounds: Boundary): Body

object Body:
  def apply(id: Int, position: Point2D, speed: Vector2D, mass: Double): Body =
    BodyImpl(id, position, speed, mass)

  private class BodyImpl(
                          override val id: Int,
                          override val position: Point2D,
                          override val speed: Vector2D,
                          override val mass: Double
                        ) extends Body:

    val repulsiveConst: Double = 0.01
    val frictionConst: Double = 1

    override def updatePosition(dt: Double): Body =
      Body(id, position.sum(this.speed.multiplyByScalar(dt)), speed, mass)

    override def checkAndSolveBoundaryCollision(bounds: Boundary): Body =
      var tmpPosition: Point2D = position
      var tmpSpeed: Vector2D = speed
      if position.x > bounds.x1 then
        tmpPosition = Point2D(bounds.x1, position.y)
        tmpSpeed = Vector2D(-speed.x, speed.y)
      else if position.x < bounds.x0 then
        tmpPosition = Point2D(bounds.x0, position.y)
        tmpSpeed = Vector2D(-speed.x, speed.y)
      if position.y > bounds.y1 then
        tmpPosition = Point2D(position.x, bounds.y1)
        tmpSpeed = Vector2D(speed.x, -speed.y)
      else if position.y < bounds.y0 then
        tmpPosition = Point2D(position.x, bounds.y0)
        tmpSpeed = Vector2D(speed.x, -speed.y)
      Body(id, tmpPosition, tmpSpeed, mass)

    override def computeRepulsiveForceBy(body: Body): Vector2D =
      val distance = distanceFrom(body)
      if distance > 0 then
        try Vector2D(body.position, position).normalize.multiplyByScalar(body.mass * repulsiveConst / (distance * distance))
        catch case _: Exception => throw InfiniteForceException()
      else throw InfiniteForceException()

    override def distanceFrom(body: Body): Double =
      val dx = position.x - body.position.x
      val dy = position.y - body.position.y
      Math.sqrt(dx * dx + dy * dy)

    override def currentFrictionForce: Vector2D =
      speed.multiplyByScalar(-frictionConst)

    override def updateSpeed(acceleration: Vector2D, dt: Double): Body =
      Body(id, position, speed.sum(acceleration.multiplyByScalar(dt)), mass)

    override def equals(obj: Any): Boolean = obj match
      case body: Body => body.id == id
      case _ => false
