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
import pcd.assignment03.distributed_programming.model.Pluviometer
import java.util.concurrent.ThreadLocalRandom
import concurrent.duration.DurationInt
import scala.concurrent.Future

enum IsAlarmResponseResult:
  case Alarm
  case NotAlarm
  case Unreachable

export IsAlarmResponseResult.*

trait PluviometerActorCommand
object Tick extends Message with PluviometerActorCommand
case class RequestIsAlarm(replyTo: ActorRef[PluviometerActorCommand]) extends Message with PluviometerActorCommand
case class IsAlarmResponse(isAlarm: IsAlarmResponseResult) extends Message with PluviometerActorCommand
case class IsMyZoneRequestPluviometer(zoneId: Int, replyTo: ActorRef[_]) extends Message with PluviometerActorCommand
case class IsMyZoneResponsePluviometer(replyTo: ActorRef[_]) extends Message with PluviometerActorCommand

val pluviometerService = ServiceKey[PluviometerActorCommand]("pluviometerService")

object PluviometerActor:

  def apply(pluviometer: Pluviometer,
            pluviometerActors: Set[ActorRef[PluviometerActorCommand]] = Set(),
            alarms: List[IsAlarmResponseResult] = List(),
            fireStationActor: Option[ActorRef[FireStationActorCommand]] = Option.empty): Behavior[PluviometerActorCommand | Receptionist.Listing] =
    Behaviors.setup[PluviometerActorCommand | Receptionist.Listing] { ctx =>
      implicit val timeout: Timeout = 1.seconds
      ctx.system.receptionist ! Receptionist.register(pluviometerService, ctx.self)
      ctx.system.receptionist ! Receptionist.Subscribe(pluviometerService, ctx.self)
      ctx.system.receptionist ! Receptionist.Subscribe(fireStationService, ctx.self)
      ctx.system.receptionist ! Receptionist.Subscribe(viewService, ctx.self)
      Behaviors.withTimers { timers =>
        timers.startTimerAtFixedRate(Tick, 5.seconds)
        Behaviors.receiveMessage {
          case msg: Receptionist.Listing => {
            ctx.log.debug(s"Received Receptionist.Listing")
            msg
              .serviceInstances(pluviometerService)
              .foreach(actor => actor ! IsMyZoneRequestPluviometer(pluviometer.zoneId, ctx.self).asInstanceOf[PluviometerActorCommand])
            msg
              .serviceInstances(fireStationService)
              .foreach(actor => actor ! IsMyZoneRequestFireStation(pluviometer.zoneId, ctx.self).asInstanceOf[FireStationActorCommand])
            msg
              .serviceInstances(viewService)
              .foreach(actor => actor ! UpdatePluviometer(pluviometer))
            Behaviors.same
          }
          case IsMyZoneRequestPluviometer(zoneId, replyTo) => {
            ctx.log.debug(s"Received IsMyZoneRequestPluviometer")
            if pluviometer.zoneId == zoneId then replyTo match
              case replyTo: ActorRef[PluviometerActorCommand] => replyTo ! IsMyZoneResponsePluviometer(ctx.self)
            Behaviors.same
          }
          case IsMyZoneResponsePluviometer(replyTo) => {
            ctx.log.debug(s"Received IsMyZoneResponsePluviometer")
            replyTo match
              case replyTo: ActorRef[FireStationActorCommand] =>
                PluviometerActor(pluviometer, pluviometerActors, alarms, Option(replyTo))
              case replyTo: ActorRef[PluviometerActorCommand] =>
                PluviometerActor(pluviometer, pluviometerActors + replyTo, alarms, fireStationActor)
          }
          case Tick => {
            ctx.log.debug(s"Received Tick")
            pluviometerActors.foreach (actor => {
              ctx.ask(actor, RequestIsAlarm.apply){
                case Success(IsAlarmResponse(isAlarm)) => IsAlarmResponse(isAlarm)
                case _ => IsAlarmResponse(Unreachable)
              }
            })
            PluviometerActor(pluviometer, pluviometerActors, List(), fireStationActor)
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
            if alarms.size == pluviometerActors.size - 1 then
              if alarms.count(isAlarm => isAlarm == Alarm) > alarms.size / 2 && fireStationActor.isDefined then
                fireStationActor.get ! WarnFireStation
              Behaviors.same
            else
              PluviometerActor(pluviometer, pluviometerActors, isAlarm :: alarms, fireStationActor)
          }
          case _ => Behaviors.stopped
        }
      }
    }
