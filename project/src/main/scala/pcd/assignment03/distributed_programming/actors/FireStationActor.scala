package pcd.assignment03.distributed_programming.actors

import akka.actor.typed.Behavior
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.Behaviors
import pcd.assignment03.distributed_programming.model.FireStation
import pcd.assignment03.distributed_programming.model.FireStation.FireStationState.*

trait FireStationActorCommand extends CommonCommands
object WarnFireStation extends Message with FireStationActorCommand
object FreeFireStation extends Message with FireStationActorCommand
object BusyFireStation extends Message with FireStationActorCommand

val fireStationService = ServiceKey[FireStationActorCommand]("fireStationService")

object FireStationActor:

  enum ZoneState:
    case Ok, Alarm, UnderManagement

  def apply(fireStation: FireStation): Behavior[FireStationActorCommand | Receptionist.Listing] =
    Behaviors.setup[FireStationActorCommand | Receptionist.Listing] { ctx =>
      ctx.system.receptionist ! Receptionist.register(fireStationService, ctx.self)
      Behaviors.receiveMessage {
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
        case IsMyZoneRequest(zoneId, replyTo) => {
          if fireStation.zoneId == zoneId then replyTo ! IsMyZoneResponse(ctx.self)
          Behaviors.same
        }
        case _ => Behaviors.stopped
      }
    }
