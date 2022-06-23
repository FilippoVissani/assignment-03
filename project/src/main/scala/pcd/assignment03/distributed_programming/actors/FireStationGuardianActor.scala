package pcd.assignment03.distributed_programming.actors

import akka.actor.typed.Behavior
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import pcd.assignment03.distributed_programming.model.{FireStation, Zone}

object FireStationGuardianActor:

  def apply(fireStation: FireStation, zone: Zone): Behavior[Nothing] =
    Behaviors.setup[Receptionist.Listing] { ctx =>
      val fireStationActor = ctx.spawnAnonymous(FireStationActor(fireStation, zone))
      ctx.system.receptionist ! Receptionist.Subscribe(viewService, ctx.self)

      Behaviors.receiveMessagePartial[Receptionist.Listing] {
        case viewService.Listing(listings) =>{
          ctx.log.debug("Received viewService")
          fireStationActor ! SetViews(listings)
          Behaviors.same
        }
        case _ => Behaviors.stopped
      }
    }.narrow
