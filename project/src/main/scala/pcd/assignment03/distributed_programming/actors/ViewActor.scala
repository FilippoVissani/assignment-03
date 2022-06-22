package pcd.assignment03.distributed_programming.actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import pcd.assignment03.distributed_programming.model.{FireStation, Pluviometer, Zone}
import pcd.assignment03.distributed_programming.view.View

trait ViewActorCommand
case class UpdatePluviometer(pluviometer: Pluviometer) extends Message with ViewActorCommand
case class UpdateZone(fireStation: FireStation, zone: Zone) extends Message with ViewActorCommand
object FixZone extends Message with ViewActorCommand
object ManageZone extends Message with ViewActorCommand
case class IsMyZoneResponseView(replyTo: ActorRef[_]) extends Message with ViewActorCommand

val viewService = ServiceKey[ViewActorCommand]("viewService")

class ViewActor(ctx: ActorContext[ViewActorCommand | Receptionist.Listing],
                zoneId: Int,
                width: Int,
                height: Int) extends AbstractBehavior(ctx):

  val view: View = View(width, height, zoneId, ctx.self)
  var fireStationActor: Option[ActorRef[FireStationActorCommand]] = Option.empty

  ctx.system.receptionist ! Receptionist.register(viewService, ctx.self)
  ctx.system.receptionist ! Receptionist.subscribe(fireStationService, ctx.self)

  override def onMessage(msg: ViewActorCommand | Receptionist.Listing): Behavior[ViewActorCommand | Receptionist.Listing] =
    msg match
      case msg: Receptionist.Listing => {
        msg.serviceInstances(fireStationService).foreach(actor => actor ! IsMyZoneRequestFireStation(zoneId, ctx.self))
      }
      case UpdatePluviometer(pluviometer) => {
        ctx.log.debug("ViewActor Received PluviometerResponse")
        view.updatePluviometer(pluviometer)
      }
      case UpdateZone(fireStation, zone) => {
        view.updateZone(zone)
        view.updateFireStation(fireStation)
      }
      case FixZone => {
        ctx.log.debug("ViewActor Received FixZone")
        if fireStationActor.isDefined then fireStationActor.get ! FreeFireStation
      }
      case ManageZone => {
        ctx.log.debug("ViewActor Received ManageZone")
        if fireStationActor.isDefined then fireStationActor.get ! BusyFireStation
      }
      case IsMyZoneResponseView(replyTo) => {
        replyTo match
          case replyTo: ActorRef[FireStationActorCommand] => fireStationActor = Option(replyTo)
      }
    this

