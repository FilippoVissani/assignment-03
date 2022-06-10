package pcd.assignment03.actor_programming.controller.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pcd.assignment03.actor_programming.controller.Chronometer
import pcd.assignment03.actor_programming.controller.actor.SimulationControllerActor.ControllerActorCommand.*

object ChronometerActor:

  enum ChronometerActorCommand:
    case Start
    case Stop
    case Duration(replyTo: ActorRef[ResponseDuration])

  export ChronometerActorCommand.*

  def apply(chronometer: Chronometer = Chronometer()): Behavior[ChronometerActorCommand] =
    Behaviors.receive((ctx, msg) => msg match
      case Start => {
        ctx.log.debug("Received Start")
        ChronometerActor(chronometer.start())
      }
      case Stop => {
        ctx.log.debug("Received Stop")
        ChronometerActor(chronometer.stop())
      }
      case Duration(replyTo: ActorRef[ResponseDuration]) => {
        ctx.log.debug("Received Duration")
        replyTo ! ResponseDuration(chronometer.duration)
        Behaviors.stopped
      }
      case _ => Behaviors.stopped
    )
