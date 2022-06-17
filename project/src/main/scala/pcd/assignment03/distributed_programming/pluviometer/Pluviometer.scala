package pcd.assignment03.distributed_programming.pluviometer

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import pcd.assignment03.distributed_programming.Point2D
import pcd.assignment03.distributed_programming.zone.ZoneActorCommand
import pcd.assignment03.distributed_programming.zone.ZoneActorCommand.*
import java.util.concurrent.ThreadLocalRandom
import concurrent.duration.DurationInt

trait Pluviometer:
  def id: Int
  def zoneId: Int
  def position: Point2D

object Pluviometer:

  def apply(id: Int, zoneId: Int, position: Point2D): Pluviometer =
    PluviometerImpl(id, zoneId, position)

  private case class PluviometerImpl(override val id: Int,
                                     override val zoneId: Int,
                                     override val position: Point2D) extends Pluviometer

object PluviometerActor:

  enum PluviometerActorCommand:
    case Tick

  export PluviometerActorCommand.*

  def apply(pluviometer: Pluviometer, zoneActor: ActorRef[ZoneActorCommand]): Behavior[PluviometerActorCommand] =
    Behaviors.setup[PluviometerActorCommand] { ctx =>
      Behaviors.withTimers { timers =>
        Behaviors.receiveMessage {
          case Tick => {
            ctx.log.debug("Received Tick")
            zoneActor ! PluviometerDetection(pluviometer.id, ThreadLocalRandom.current().nextFloat(20))
            timers.startSingleTimer(Tick, 5000.millis)
            Behaviors.same
          }
          case _ => Behaviors.stopped
        }
      }
    }
