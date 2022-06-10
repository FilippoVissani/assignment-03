package pcd.assignment03.actor_programming.boundary.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import pcd.assignment03.actor_programming.app.RootActor.RootActorCommand
import pcd.assignment03.actor_programming.app.RootActor.RootActorCommand.*
import pcd.assignment03.actor_programming.boundary.actor.ViewActor.ViewActorCommand.{StartClick, StopClick}
import pcd.assignment03.actor_programming.boundary.{SwingSimulationGUI, View}
import pcd.assignment03.actor_programming.control.actor.SimulationControllerActor.ControllerActorCommand
import pcd.assignment03.actor_programming.control.actor.SimulationControllerActor.ControllerActorCommand.*
import pcd.assignment03.actor_programming.util.{Boundary, Point2D}

import scala.language.postfixOps

object ViewActor:

  enum ViewActorCommand:
    case Display(bodiesPositions: List[Point2D], virtualTime: Double, currentIteration: Long)
    case StartClick
    case StopClick

  export ViewActorCommand.*

  def apply(controllerActor: ActorRef[ControllerActorCommand], bounds: Boundary, width: Int, height: Int): Behavior[ViewActorCommand] =
    Behaviors.setup(ctx =>

      val view: View = View(ctx.self, bounds, width, height)

      Behaviors.receiveMessage(msg => msg match
        case Display(bodiesPositions: List[Point2D], virtualTime: Double, currentIteration: Long) => {
          ctx.log.debug("ViewActor Received Display")
          view.display(bodiesPositions, virtualTime, currentIteration)
          Behaviors.same
        }
        case StartClick => {
          ctx.log.debug("ViewActor Received Start")
          controllerActor ! StartSimulation
          Behaviors.same
        }
        case StopClick => {
          ctx.log.debug("ViewActor Received Stop")
          controllerActor ! StopSimulation
          Behaviors.same
        }
      )
    )


