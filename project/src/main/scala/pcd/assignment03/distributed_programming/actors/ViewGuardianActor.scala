package pcd.assignment03.distributed_programming.actors

import akka.actor.typed.Behavior
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import pcd.assignment03.distributed_programming.model.Pluviometer

object ViewGuardianActor:
  def apply(zoneId: Int,
            width: Int,
            height: Int): Behavior[Nothing] = 
    Behaviors.setup[Receptionist.Listing] { ctx =>
      val viewActor = ctx.spawnAnonymous(Behaviors.setup(new ViewActor(_, zoneId, width, height)))
      ctx.system.receptionist ! Receptionist.subscribe(fireStationService, ctx.self)

      Behaviors.receiveMessagePartial[Receptionist.Listing] {
        case fireStationService.Listing(listings) =>{
          ctx.log.debug("Received fireStationService")
          listings.foreach(actor => actor ! IsMyZoneRequestFireStation(zoneId, viewActor))
          Behaviors.same
        }
        case _ => Behaviors.stopped
      }
    }.narrow

