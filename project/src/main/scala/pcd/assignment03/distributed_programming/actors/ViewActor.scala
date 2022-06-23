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
case class IsMyZoneResponseView(replyTo: ActorRef[FireStationActorCommand]) extends Message with ViewActorCommand

val viewService = ServiceKey[ViewActorCommand]("viewService")

class ViewActor(ctx: ActorContext[ViewActorCommand],
                zoneId: Int,
                width: Int,
                height: Int) extends AbstractBehavior(ctx):

  val view: View = View(width, height, zoneId, ctx.self)
  var fireStationActor: Option[ActorRef[FireStationActorCommand]] = Option.empty

  ctx.system.receptionist ! Receptionist.register(viewService, ctx.self)

  override def onMessage(msg: ViewActorCommand): Behavior[ViewActorCommand] =
    msg match
      case UpdatePluviometer(pluviometer) => {
        ctx.log.debug("Received UpdatePluviometer")
        view.updatePluviometer(pluviometer)
      }
      case UpdateZone(fireStation, zone) => {
        ctx.log.debug("Received UpdateZone")
        view.updateZone(zone)
        view.updateFireStation(fireStation)
      }
      case FixZone => {
        ctx.log.debug("Received FixZone")
        if fireStationActor.isDefined then fireStationActor.get ! FreeFireStation
      }
      case ManageZone => {
        ctx.log.debug("Received ManageZone")
        if fireStationActor.isDefined then fireStationActor.get ! BusyFireStation
      }
      case IsMyZoneResponseView(replyTo) => {
        ctx.log.debug("Received IsMyZoneResponseView")
        fireStationActor = Option(replyTo)
      }
    this

