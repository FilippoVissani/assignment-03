package pcd.assignment03.actor_programming.entity.logic

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import pcd.assignment03.actor_programming.entity.logic.exception.InfiniteForceException
import scala.language.postfixOps

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
   * @param timeStep to be multiplied by speed and added to position
   */
  def updatePosition(timeStep: Double): Body

  /**
   * @param bodies
   * @param timeStep to be multiplied by acceleration and added to speed
   */
  def updateSpeed(bodies: List[Body], timeStep: Double): Body

  /**
   * Solve collisions with boundary
   */
  def checkAndSolveBoundaryCollision(): Body

object Body:

  def apply(id: Int, position: Point2D, speed: Vector2D, mass: Double, bounds: Boundary): Body =
    BodyImpl(id, position, speed, mass, bounds)

  private class BodyImpl(
                          override val id: Int,
                          override val position: Point2D,
                          override val speed: Vector2D,
                          override val mass: Double,
                          val bounds: Boundary
                        ) extends Body:

    val repulsiveConst: Double = 0.01
    val frictionConst: Double = 1

    override def updatePosition(timeStep: Double): Body =
      Body(id, position.sum(speed.multiplyByScalar(timeStep)), speed, mass, bounds).checkAndSolveBoundaryCollision()

    override def updateSpeed(bodies: List[Body], timeStep: Double): Body =
      Body(id, position, speed.sum(computeAcceleration(bodies).multiplyByScalar(timeStep)), mass, bounds)

    override def checkAndSolveBoundaryCollision(): Body =
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
      Body(id, tmpPosition, tmpSpeed, mass, bounds)

    override def equals(obj: Any): Boolean = obj match
      case body: Body => body.id == id
      case _ => false

    private def computeAcceleration(bodies: List[Body]): Vector2D =
      var totalForce: Vector2D = Vector2D(0, 0)
      for (otherBody <- bodies)
        if this != otherBody then
          try
            val forceByOtherBody = computeRepulsiveForceBy(otherBody)
            totalForce = totalForce.sum(forceByOtherBody)
          catch
            case _: Exception => throw new InfiniteForceException
        else throw new InfiniteForceException
      totalForce = totalForce.sum(currentFrictionForce)
      totalForce.multiplyByScalar(1.0 / mass)

    private def currentFrictionForce: Vector2D =
      speed.multiplyByScalar(-frictionConst)

    private def computeRepulsiveForceBy(body: Body): Vector2D =
      val distance = distanceFrom(body)
      if distance > 0 then
        try Vector2D(body.position, position).normalize.multiplyByScalar(body.mass * repulsiveConst / (distance * distance))
        catch case _: Exception => throw InfiniteForceException()
      else throw InfiniteForceException()

    private def distanceFrom(body: Body): Double =
      val dx = position.x - body.position.x
      val dy = position.y - body.position.y
      Math.sqrt(dx * dx + dy * dy)
