package pcd.assignment03.actor_programming.boundary

import akka.actor.typed.ActorRef
import pcd.assignment03.actor_programming.boundary.actor.ViewActor.ViewActorCommand
import pcd.assignment03.actor_programming.boundary.actor.ViewActor.ViewActorCommand.*
import pcd.assignment03.actor_programming.entity.Body
import pcd.assignment03.actor_programming.util.{Boundary, Point2D}


/**
 * Trait used to represent view in MVC
 */
trait View:
  /**
   * @param bodiesPositions  current position of the bodies
   * @param virtualTime      actual virtual time
   * @param currentIteration current iteration
   *
   */
  def display(bodiesPositions: List[Point2D], virtualTime: Double, currentIteration: Long): Unit

  /**
   * Stop the simulation
   */
  def stopSimulation(): Unit

  /**
   * Start the simulation
   */
  def startSimulation(): Unit
  def bounds: Boundary

object View:
  def apply(viewActor: ActorRef[ViewActorCommand], bounds: Boundary, width: Int, height: Int): View =
    ViewImpl(viewActor, bounds, width, height)

  /**
   * Implementation of View trait
   */
  private class ViewImpl(viewActor: ActorRef[ViewActorCommand],
                         override val  bounds: Boundary,
                         val width: Int,
                         val height: Int) extends View:
    val frame: SwingSimulationGUI = new SwingSimulationGUI(this, width, height)

    override def display(bodiesPositions: List[Point2D], virtualTime: Double, currentIteration: Long): Unit =
      frame.display(bodiesPositions, virtualTime, currentIteration)

    override def stopSimulation(): Unit =
      viewActor ! StopClick

    override def startSimulation(): Unit =
      viewActor ! StartClick
