package pcd.assignment03.distributed_programming.actors

import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import pcd.assignment03.distributed_programming.model.Zone
import pcd.assignment03.distributed_programming.model.Zone.ZoneState.*
import pcd.assignment03.distributed_programming.actors.Message
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Terminated}

trait ZoneActorCommand
case class PluviometerDetection(waterQuantity: Float, replyTo: ActorRef[_]) extends Message with ZoneActorCommand
case class ManageZone(replyTo: ActorRef[_]) extends Message with ZoneActorCommand
case class FixZone(replyTo: ActorRef[_]) extends Message with ZoneActorCommand

val zoneService = ServiceKey[ZoneActorCommand]("ZoneService")

class ZoneActor(val ctx: ActorContext[ZoneActorCommand],
                var zone: Zone,
                val pluviometers: Int) extends AbstractBehavior(ctx):

  var pluviomentersDetections: Map[ActorRef[_], Float] = Map()
  ctx.system.receptionist ! Receptionist.Register(zoneService, ctx.self)

  override def onMessage(msg: ZoneActorCommand): Behavior[ZoneActorCommand] =
    msg match
      case PluviometerDetection(waterQuantity: Float, replyTo: ActorRef[_]) => {
        ctx.log.debug("ZoneActor received PluviometerDetection")
        if zone.state != UnderManagement then
          pluviomentersDetections = pluviomentersDetections + (replyTo -> waterQuantity)
          //TODO: anche se il numero totale di pluviometri è fissato, ognuno di essi può fallire, quindi la maggioranza per una certa zona può cambiare nel tempo.
          if pluviometers / 2 < pluviomentersDetections.values.count(d => d > zone.threshold) then
            zone = zone.state_(Alarm)
        //fireStation ! WarnFireStation
      }
      case ManageZone(replyTo: ActorRef[_]) => {
        zone = zone.state_(UnderManagement)
      }
      case FixZone(replyTo: ActorRef[_]) => {
        pluviomentersDetections = Map()
        zone = zone.state_(Ok)
      }
    this


