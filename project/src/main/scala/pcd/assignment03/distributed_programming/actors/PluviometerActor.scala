package pcd.assignment03.distributed_programming.actors

import akka.actor

import scala.util.{Failure, Success}
import akka.pattern.ask
import akka.actor.{AbstractActorWithTimers, Actor, Timers}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors, TimerScheduler}
import akka.util.Timeout
import pcd.assignment03.distributed_programming.actors.IsAlarmResponseResult.*
import pcd.assignment03.distributed_programming.model.Pluviometer

import java.util.concurrent.ThreadLocalRandom
import concurrent.duration.DurationInt
import scala.concurrent.Future

object IsAlarmResponseResult extends Enumeration:
  type IsAlarmResponseResult = Value
  val Alarm, NotAlarm, Unreachable = Value


trait PluviometerActorCommand
object Tick extends Serializable with PluviometerActorCommand
case class RequestIsAlarm(replyTo: ActorRef[PluviometerActorCommand]) extends Serializable with PluviometerActorCommand
case class IsAlarmResponse(isAlarm: IsAlarmResponseResult) extends Serializable with PluviometerActorCommand
case class IsMyZoneRequestPluviometer(zoneId: Int, replyTo: ActorRef[PluviometerActorCommand]) extends Serializable with PluviometerActorCommand
case class IsMyZoneResponseFromFireStationToPluviometer(replyTo: ActorRef[FireStationActorCommand]) extends Serializable with PluviometerActorCommand
case class IsMyZoneResponseFromPluviometerToPluviometer(replyTo: ActorRef[PluviometerActorCommand]) extends Serializable with PluviometerActorCommand


val pluviometerService = ServiceKey[PluviometerActorCommand]("pluviometerService")

object PluviometerActor:

  def apply(pluviometer: Pluviometer,
            pluviometerActors: Set[ActorRef[PluviometerActorCommand]] = Set(),
            alarms: List[IsAlarmResponseResult] = List(),
            fireStationActor: Option[ActorRef[FireStationActorCommand]] = Option.empty): Behavior[PluviometerActorCommand] =
    Behaviors.setup[PluviometerActorCommand] { ctx =>
      ctx.system.receptionist ! Receptionist.register(pluviometerService, ctx.self)
      Behaviors.withTimers { timers =>
        timers.startTimerAtFixedRate(Tick, 5.seconds)
        PluviometerActorLogic(ctx, pluviometer, pluviometerActors, alarms, fireStationActor)
      }
    }

  def PluviometerActorLogic(ctx: ActorContext[PluviometerActorCommand],
                            pluviometer: Pluviometer,
                            pluviometerActors: Set[ActorRef[PluviometerActorCommand]] = Set(),
                            alarms: List[IsAlarmResponseResult] = List(),
                            fireStationActor: Option[ActorRef[FireStationActorCommand]] = Option.empty): Behavior[PluviometerActorCommand] =
    implicit val timeout: Timeout = 2.seconds
    Behaviors.receiveMessage {
      case IsMyZoneRequestPluviometer(zoneId, replyTo) => {
        ctx.log.debug(s"Received IsMyZoneRequestPluviometer")
        if pluviometer.zoneId == zoneId then
          replyTo ! IsMyZoneResponseFromPluviometerToPluviometer(ctx.self)
        Behaviors.same
      }
      case IsMyZoneResponseFromFireStationToPluviometer(replyTo) => {
        ctx.log.debug(s"Received IsMyZoneResponseFromFireStationToPluviometer")
        PluviometerActorLogic(ctx, pluviometer, pluviometerActors, alarms, Option(replyTo))
      }
      case IsMyZoneResponseFromPluviometerToPluviometer(replyTo) => {
        ctx.log.debug(s"Received IsMyZoneResponseFromPluviometerToPluviometer")
        PluviometerActorLogic(ctx, pluviometer, pluviometerActors + replyTo, alarms, fireStationActor)
      }
      case Tick => {
        ctx.log.debug(s"Received Tick")
        pluviometerActors.foreach (actor => {
          ctx.ask(actor, RequestIsAlarm.apply){
            case Success(IsAlarmResponse(isAlarm)) => IsAlarmResponse(isAlarm)
            case _ => IsAlarmResponse(Unreachable)
          }
        })
        PluviometerActorLogic(ctx, pluviometer, pluviometerActors, List(), fireStationActor)
      }
      case RequestIsAlarm(replyTo) => {
        ctx.log.debug(s"Received RequestIsAlarm")
        if ThreadLocalRandom.current().nextFloat(20) > pluviometer.threshold then
          replyTo ! IsAlarmResponse(Alarm)
        else
          replyTo ! IsAlarmResponse(NotAlarm)
        Behaviors.same
      }
      case IsAlarmResponse(isAlarm) => {
        ctx.log.debug(s"Received IsAlarmResponse")
        val tmpAlarms = isAlarm :: alarms
        if tmpAlarms.size == pluviometerActors.size then
          if tmpAlarms.count(state => state == Alarm) > tmpAlarms.size / 2 && fireStationActor.isDefined then
            fireStationActor.get ! WarnFireStation
          Behaviors.same
        else
          PluviometerActorLogic(ctx, pluviometer, pluviometerActors, tmpAlarms, fireStationActor)
      }
      case _ => Behaviors.stopped
    }

