package pcd.assignment03.actor_programming.entity.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pcd.assignment03.actor_programming.entity.Body
import pcd.assignment03.actor_programming.control.actor.SimulationControllerActor.ControllerActorCommand.*

object BodyActor:

  enum BodyActorCommand:
    case RequestBody(replyTo: ActorRef[ResponseBody])
    case UpdatePosition(bodies: List[Body], timeStep: Double, replyTo: ActorRef[PositionUpdated])
    case ResponseStartTime()

  export BodyActorCommand.*

  def apply(body: Body): Behavior[BodyActorCommand] =
    Behaviors.receive((context, msg) => msg match
      case RequestBody(replyTo: ActorRef[ResponseBody]) => {
        context.log.debug("Received RequestBody")
        replyTo ! ResponseBody(body)
        Behaviors.same
      }
      case UpdatePosition(bodies: List[Body], timeStep: Double, replyTo: ActorRef[PositionUpdated]) => {
        context.log.debug("Received UpdatePosition")
        val tmpBody = body.updateSpeed(bodies, timeStep).updatePosition(timeStep)
        replyTo ! PositionUpdated(tmpBody)
        BodyActor(tmpBody)
      }
      case _ => {
        context.log.debug("Received Stop")
        Behaviors.stopped
      }
    )