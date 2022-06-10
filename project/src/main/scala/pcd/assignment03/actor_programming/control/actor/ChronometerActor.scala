package pcd.assignment03.actor_programming.control.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pcd.assignment03.actor_programming.control.Chronometer
import pcd.assignment03.actor_programming.control.actor.SimulationControllerActor.ControllerActorCommand.*

object ChronometerActor:

  enum ChronometerActorCommand:
    case Start
    case Stop
    case Duration(replyTo: ActorRef[ResponseDuration])

  export ChronometerActorCommand.*

  def apply(chronometer: Chronometer = Chronometer()): Behavior[ChronometerActorCommand] =
    Behaviors.receive((context, msg) => msg match
      case Start => {
        context.log.debug("Received Start")
        ChronometerActor(chronometer.start())
      }
      case Stop => {
        context.log.debug("Received Stop")
        ChronometerActor(chronometer.stop())
      }
      case Duration(replyTo: ActorRef[ResponseDuration]) => {
        context.log.debug("Received Duration")
        replyTo ! ResponseDuration(chronometer.duration)
        Behaviors.stopped
      }
      case _ => Behaviors.stopped
    )
