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
case class IsMyZoneRequestFromViewToFireStation(zoneId: Int, replyTo: ActorRef[ViewActorCommand]) extends Message with FireStationActorCommand
case class IsMyZoneRequestFromPluviometerToFireStation(zoneId: Int, replyTo: ActorRef[PluviometerActorCommand]) extends Message with FireStationActorCommand
case class SetViews(views: Set[ActorRef[ViewActorCommand]]) extends Message with FireStationActorCommand

val fireStationService = ServiceKey[FireStationActorCommand]("fireStationService")

object FireStationActor:
  def apply(fireStation: FireStation,
            zone: Zone,
            viewActors: Set[ActorRef[ViewActorCommand]] = Set()): Behavior[FireStationActorCommand] =
    viewActors.foreach(viewActor => viewActor ! UpdateZone(fireStation, zone))
    Behaviors.setup[FireStationActorCommand] { ctx =>
      ctx.system.receptionist ! Receptionist.Register(fireStationService, ctx.self)
      Behaviors.receiveMessage {
        case SetViews(views) => {
          ctx.log.debug("Received SetViews")
          FireStationActor(fireStation, zone, views)
        }
        case WarnFireStation => {
          ctx.log.debug("Received WarnFireStation")
          if fireStation.state == Free then {
            FireStationActor(fireStation.state_(Warned), zone.state_(ZoneState.Alarm), viewActors)
          }
          else Behaviors.same
        }
        case FreeFireStation => {
          ctx.log.debug("Received FreeFireStation")
          FireStationActor(fireStation.state_(Free), zone.state_(ZoneState.Ok), viewActors)
        }
        case BusyFireStation => {
          ctx.log.debug("Received BusyFireStation")
          FireStationActor(fireStation.state_(Busy), zone.state_(ZoneState.UnderManagement), viewActors)
        }
        case IsMyZoneRequestFromViewToFireStation(zoneId, replyTo) => {
          ctx.log.debug("Received IsMyZoneRequestFireStation")
          if fireStation.zoneId == zoneId then
            replyTo ! IsMyZoneResponseView(ctx.self)
          Behaviors.same
        }
        case IsMyZoneRequestFromPluviometerToFireStation(zoneId, replyTo) => {
          ctx.log.debug("Received IsMyZoneRequestFireStation")
          if fireStation.zoneId == zoneId then
            replyTo ! IsMyZoneResponsePluviometer(ctx.self)
          Behaviors.same
        }
        case _ => {
          Behaviors.stopped
        }
      }
    }
