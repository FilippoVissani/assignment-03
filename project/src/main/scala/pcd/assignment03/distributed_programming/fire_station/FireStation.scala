package pcd.assignment03.distributed_programming.fire_station

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pcd.assignment03.distributed_programming.Point2D
import concurrent.duration.DurationInt
import scala.util.Random

/*
Fire station logic
* */
enum FireStationState:
   case Free, Busy, Warned

trait FireStation:
   def zoneId: Int
   def position: Point2D
   def state: FireStationState
   def state_(newState: FireStationState): FireStation

object FireStation:

   def apply(zoneId: Int, position: Point2D, state: FireStationState): FireStation =
      FireStationImpl(zoneId, position, state)

   private case class FireStationImpl(override val zoneId: Int,
                                      override val position: Point2D,
                                      override val state: FireStationState) extends FireStation:

      override def state_(newState: FireStationState): FireStation =
         FireStation(zoneId, position, newState)

/*
Fire station actor
* */
object FireStationActor:
   import pcd.assignment03.distributed_programming.fire_station.FireStationState.*

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
