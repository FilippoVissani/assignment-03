package pcd.assignment03.actor_programming.model.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pcd.assignment03.actor_programming.controller.actor.SimulationControllerActor.ControllerActorCommand.*
import pcd.assignment03.actor_programming.model.Body

object BodyActor:

  enum BodyActorCommand:
    case RequestBody(replyTo: ActorRef[ResponseBody])
    case UpdatePosition(bodies: List[Body], timeStep: Double, replyTo: ActorRef[PositionUpdated])
    case StopBodyActor

  export BodyActorCommand.*

  def apply(body: Body): Behavior[BodyActorCommand] =
    Behaviors.receive((ctx, msg) => msg match
      case RequestBody(replyTo: ActorRef[ResponseBody]) => {
        ctx.log.debug("Received RequestBody")
        replyTo ! ResponseBody(body)
        Behaviors.same
      }
      case UpdatePosition(bodies: List[Body], timeStep: Double, replyTo: ActorRef[PositionUpdated]) => {
        ctx.log.debug("Received UpdatePosition")
        val tmpBody = body.updateSpeed(bodies, timeStep).updatePosition(timeStep)
        replyTo ! PositionUpdated(tmpBody)
        BodyActor(tmpBody)
      }
      case _ => {
        ctx.log.debug("Received Stop")
        Behaviors.stopped
      }
    )