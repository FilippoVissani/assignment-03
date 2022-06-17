package pcd.assignment03.distributed_programming.actors

import pcd.assignment03.distributed_programming.model.Zone
import pcd.assignment03.distributed_programming.model.Zone.ZoneState.*
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Terminated}

enum ZoneActorCommand:
  case PluviometerDetection(pluviometerId: Int, waterQuantity: Float)
  case ManageZone
  case FixZone

export ZoneActorCommand.*

class ZoneActor(val ctx: ActorContext[ZoneActorCommand],
                var zone: Zone,
                val pluviometers: Int) extends AbstractBehavior(ctx):

  var pluviomentersDetections: Map[Int, Float] = Map()

  override def onMessage(msg: ZoneActorCommand): Behavior[ZoneActorCommand] =
    msg match
      case PluviometerDetection(pluviometerId: Int, waterQuantity: Float) => {
        ctx.log.debug("ZoneActor received PluviometerDetection")
        if zone.state != UnderManagement then
          pluviomentersDetections = pluviomentersDetections + (pluviometerId -> waterQuantity)
          //TODO: anche se il numero totale di pluviometri è fissato, ognuno di essi può fallire, quindi la maggioranza per una certa zona può cambiare nel tempo.
          if pluviometers / 2 < pluviomentersDetections.values.count(d => d > zone.threshold) then
            zone = zone.state_(Alarm)
            //fireStation ! WarnFireStation
      }
      case ManageZone => {
        zone = zone.state_(UnderManagement)
      }
      case FixZone => {
        pluviomentersDetections = Map()
        zone = zone.state_(Ok)
      }
    this

