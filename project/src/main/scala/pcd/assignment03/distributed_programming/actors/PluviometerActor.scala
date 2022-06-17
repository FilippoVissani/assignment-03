package pcd.assignment03.distributed_programming.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import pcd.assignment03.distributed_programming.model.Pluviometer

import concurrent.duration.DurationInt

object PluviometerActor:

  enum PluviometerActorCommand:
    case Tick

  export PluviometerActorCommand.*

  def apply(pluviometer: Pluviometer, actorZone: Option[ActorRef[ZoneActorCommand]] = Option.empty): Behavior[PluviometerActorCommand | Receptionist.Listing] =
    Behaviors.setup[PluviometerActorCommand | Receptionist.Listing] { ctx =>
      ctx.system.receptionist ! Receptionist.Subscribe(zoneService, ctx.self)
      Behaviors.withTimers { timers =>
        Behaviors.receiveMessage {
          case msg: Receptionist.Listing => {
            val actorZone = msg.serviceInstances(zoneService).toList.filter(x => x.path.name == pluviometer.zoneId).head
            PluviometerActor(pluviometer, Option(actorZone))
          }
          case Tick => {
            ctx.log.debug("Received Tick")
            //zoneActor ! PluviometerDetection(pluviometer.id, ThreadLocalRandom.current().nextFloat(20))
            timers.startSingleTimer(Tick, 5000.millis)
            Behaviors.same
          }
          case _ => Behaviors.stopped
        }
      }
    }

