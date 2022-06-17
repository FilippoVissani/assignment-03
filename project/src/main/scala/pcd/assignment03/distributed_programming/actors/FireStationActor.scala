package pcd.assignment03.distributed_programming.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pcd.assignment03.distributed_programming.model.FireStation
import pcd.assignment03.distributed_programming.model.FireStation.FireStationState.*

object FireStationActor:

  enum FireStationActorCommand:
    case WarnFireStation
    case FreeFireStation
    case BusyFireStation

  export FireStationActorCommand.*

  def apply(fireStation: FireStation): Behavior[FireStationActorCommand] =
    Behaviors.receive { (ctx, msg) => msg match
      case WarnFireStation => {
        ctx.log.debug("Received WarnFireStation")
        FireStationActor(fireStation.state_(Warned))
      }
      case FreeFireStation => {
        ctx.log.debug("Received FreeFireStation")
        FireStationActor(fireStation.state_(Free))
      }
      case BusyFireStation => {
        ctx.log.debug("Received BusyFireStation")
        FireStationActor(fireStation.state_(Busy))
      }
      case _ => Behaviors.stopped
    }
