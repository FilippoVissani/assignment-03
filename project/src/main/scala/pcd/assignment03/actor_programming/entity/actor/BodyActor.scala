package pcd.assignment03.actor_programming.entity.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pcd.assignment03.actor_programming.entity.logic.Body
import pcd.assignment03.actor_programming.control.SimulationControllerActor.ResponseBody

object BodyActor:

  enum BodyActorCommand:
    case RequestBody(replyTo: ActorRef[ResponseBody])
    case UpdateSpeed(bodies: List[Body], timeStep: Double)
    case UpdatePosition(timeStep: Double)

  export BodyActorCommand.*

  def apply(body: Body): Behavior[BodyActorCommand] =
    Behaviors.receive((context, msg) => msg match
      case RequestBody(replyTo: ActorRef[ResponseBody]) => {
        context.log.debug("Received RequestBody")
        replyTo ! ResponseBody(body)
        Behaviors.same
      }
      case UpdateSpeed(bodies: List[Body], timeStep: Double) => {
        context.log.debug("Received UpdateSpeed")
        BodyActor(body.updateSpeed(bodies, timeStep))
      }
      case UpdatePosition(timeStep: Double) => {
        context.log.debug("Received UpdatePosition")
        BodyActor(body.updatePosition(timeStep))
      }
      case _ => {
        context.log.debug("Received Stop")
        Behaviors.stopped
      }
    )