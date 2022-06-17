package pcd.assignment03.distributed_programming.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pcd.assignment03.distributed_programming.model.Pluviometer
import concurrent.duration.DurationInt

object PluviometerActor:

  enum PluviometerActorCommand:
    case Tick

  export PluviometerActorCommand.*

  def apply(pluviometer: Pluviometer): Behavior[PluviometerActorCommand] =
    Behaviors.setup[PluviometerActorCommand] { ctx =>
      Behaviors.withTimers { timers =>
        Behaviors.receiveMessage {
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

