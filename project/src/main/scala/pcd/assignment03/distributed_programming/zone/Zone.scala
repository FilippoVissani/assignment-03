package pcd.assignment03.distributed_programming.zone

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Terminated}
import pcd.assignment03.distributed_programming.Boundary
import pcd.assignment03.distributed_programming.fire_station.FireStationActor.FireStationActorCommand
import pcd.assignment03.distributed_programming.fire_station.FireStationActor.FireStationActorCommand.*
import pcd.assignment03.distributed_programming.pluviometer.Pluviometer

enum ZoneState:
  case Ok, Alarm, UnderManagement

trait Zone:
  def id: Int
  def bounds: Boundary
  def state: ZoneState
  def threshold: Float
  def state_(newState: ZoneState): Zone

object Zone:

  def apply(id: Int, bounds: Boundary, state: ZoneState, threshold: Float): Zone =
    ZoneImpl(id, bounds, state, threshold)

  private case class ZoneImpl(override val id: Int,
                              override val bounds: Boundary,
                              override val state: ZoneState,
                              override val threshold: Float) extends Zone:
    override def state_(newState: ZoneState): Zone =
      Zone(id, bounds, newState, threshold)

/** Actor */
enum ZoneActorCommand:
  case PluviometerDetection(pluviometerId: Int, waterQuantity: Float)
  case ManageZone
  case FixZone

export ZoneActorCommand.*

class ZoneActor(val ctx: ActorContext[ZoneActorCommand],
                var zone: Zone,
                val fireStation: ActorRef[FireStationActorCommand],
                val pluviometers: Int) extends AbstractBehavior(ctx):

  var pluviomentersDetections: Map[Int, Float] = Map()

  override def onMessage(msg: ZoneActorCommand): Behavior[ZoneActorCommand] =
    msg match
      case PluviometerDetection(pluviometerId: Int, waterQuantity: Float) => {
        ctx.log.debug("ZoneActor received PluviometerDetection")
        if zone.state != ZoneState.UnderManagement then
          pluviomentersDetections = pluviomentersDetections + (pluviometerId -> waterQuantity)
          //TODO NB: anche se il numero totale di pluviometri è fissato, ognuno di essi può fallire, quindi la maggioranza per una certa zona può cambiare nel tempo.
          if pluviometers / 2 < pluviomentersDetections.values.count(d => d > zone.threshold) then
            zone = zone.state_(ZoneState.Alarm)
            fireStation ! WarnFireStation
      }
      case ManageZone => {
        zone = zone.state_(ZoneState.UnderManagement)
      }
      case FixZone => {
        pluviomentersDetections = Map()
        zone = zone.state_(ZoneState.Ok)
      }
    this
