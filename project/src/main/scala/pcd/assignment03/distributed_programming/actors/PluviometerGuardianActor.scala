package pcd.assignment03.distributed_programming.actors

import akka.actor.typed.Behavior
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import pcd.assignment03.distributed_programming.model.Pluviometer

object PluviometerGuardianActor:
  def apply(pluviometer: Pluviometer): Behavior[Nothing] =
    Behaviors.setup[Receptionist.Listing] { ctx =>
      val pluviometerActor = ctx.spawnAnonymous(PluviometerActor(pluviometer))
      ctx.system.receptionist ! Receptionist.Subscribe(pluviometerService, ctx.self)
      ctx.system.receptionist ! Receptionist.Subscribe(fireStationService, ctx.self)
      ctx.system.receptionist ! Receptionist.Subscribe(viewService, ctx.self)

      Behaviors.receiveMessagePartial[Receptionist.Listing] {
        case pluviometerService.Listing(listings) => {
          ctx.log.debug("Received pluviometerService")
          listings.foreach(actor => actor ! IsMyZoneRequestPluviometer(pluviometer.zoneId, pluviometerActor))
          Behaviors.same
        }
        case fireStationService.Listing(listings) =>{
          ctx.log.debug("Received fireStationService")
          listings.foreach(actor => actor ! IsMyZoneRequestFromPluviometerToFireStation(pluviometer.zoneId, pluviometerActor))
          Behaviors.same
        }
        case viewService.Listing(listings) =>{
          ctx.log.debug("Received viewService")
          listings.foreach(actor => actor ! UpdatePluviometer(pluviometer))
          Behaviors.same
        }
        case _ => Behaviors.stopped
      }
    }.narrow
