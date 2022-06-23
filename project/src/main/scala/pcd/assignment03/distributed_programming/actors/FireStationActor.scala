package pcd.assignment03.distributed_programming.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.Behaviors
import pcd.assignment03.distributed_programming.model.{FireStation, Zone}
import pcd.assignment03.distributed_programming.model.FireStation.FireStationState.*
import pcd.assignment03.distributed_programming.model.Zone.ZoneState

trait FireStationActorCommand
object WarnFireStation extends Message with FireStationActorCommand
object FreeFireStation extends Message with FireStationActorCommand
object BusyFireStation extends Message with FireStationActorCommand
case class IsMyZoneRequestFireStation(zoneId: Int, replyTo: ActorRef[_]) extends Message with FireStationActorCommand

val fireStationService = ServiceKey[FireStationActorCommand]("fireStationService")

object FireStationActor:
  def apply(fireStation: FireStation,
            zone: Zone,
            viewActors: Set[ActorRef[ViewActorCommand]] = Set()): Behavior[FireStationActorCommand | Receptionist.Listing] =
    viewActors.foreach(viewActor => viewActor ! UpdateZone(fireStation, zone))
    Behaviors.setup[FireStationActorCommand | Receptionist.Listing] { ctx =>
      ctx.system.receptionist ! Receptionist.Register(fireStationService, ctx.self)
      ctx.system.receptionist ! Receptionist.Subscribe(viewService, ctx.self)
      Behaviors.receiveMessage {
        case msg: Receptionist.Listing => {
          ctx.log.debug("Received Receptionist.Listing")
          //TODO FireStationActor(fireStation, zone, msg.serviceInstances(viewService))
          Behaviors.same //TODO
        }
        case WarnFireStation => {
          ctx.log.debug("Received WarnFireStation")
          //TODO if fireStation.state == Free then FireStationActor(fireStation.state_(Warned), zone.state_(ZoneState.Alarm), viewActors)
          //else Behaviors.same
          Behaviors.same //TODO
        }
        case FreeFireStation => {
          ctx.log.debug("Received FreeFireStation")
          //TODO FireStationActor(fireStation.state_(Free), zone.state_(ZoneState.Ok), viewActors)
          Behaviors.same //TODO
        }
        case BusyFireStation => {
          ctx.log.debug("Received BusyFireStation")
          //TODO FireStationActor(fireStation.state_(Busy), zone.state_(ZoneState.UnderManagement), viewActors)
          Behaviors.same //TODO
        }
        case IsMyZoneRequestFireStation(zoneId, replyTo) => {
          ctx.log.debug("Received IsMyZoneRequestFireStation")
          /*TODOif fireStation.zoneId == zoneId then replyTo match
            case replyTo: ActorRef[PluviometerActorCommand] => replyTo ! IsMyZoneResponsePluviometer(ctx.self)
            case replyTo: ActorRef[ViewActorCommand] => replyTo ! IsMyZoneResponseView(ctx.self)*/
          Behaviors.same
        }
        case _ => {
          //TODO Behaviors.stopped
          Behaviors.same //TODO
        }
      }
    }
