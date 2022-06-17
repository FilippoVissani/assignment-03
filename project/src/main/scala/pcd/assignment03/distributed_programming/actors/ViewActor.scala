package pcd.assignment03.distributed_programming.actors

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pcd.assignment03.distributed_programming.model.{FireStation, Pluviometer, Zone}
import pcd.assignment03.distributed_programming.view.View

object ViewActor:
  enum ViewActorCommand:
    case PluviometerResponse(pluviometer: Pluviometer)
    case FireStationResponse(fireStation: FireStation)
    case ZoneResponse(zone: Zone)
    case FixZone
    case ManageZone

  export ViewActorCommand.*

  def apply(width: Int, height: Int): Behavior[ViewActorCommand] =
    Behaviors.setup(ctx => {
      
      val view: View = View(width, height, ctx.self)

      Behaviors.receiveMessage(msg => msg match
        case PluviometerResponse(pluviometer: Pluviometer) => {
          ctx.log.debug("ViewActor Received PluviometerResponse")
          view.displayPluviometer(pluviometer)
          Behaviors.same
        }
        case FireStationResponse(fireStation: FireStation) => {
          ctx.log.debug("ViewActor Received FireStationResponse")
          view.displayFireStation(fireStation)
          Behaviors.same
        }
        case ZoneResponse(zone: Zone) => {
          ctx.log.debug("ViewActor Received ZoneResponse")
          view.displayZone(zone)
          Behaviors.same
        }
        case FixZone => {
          ctx.log.debug("ViewActor Received FixZone")
          // TODO: FIXARE ZONA
          Behaviors.same
        }
        case ManageZone => {
          ctx.log.debug("ViewActor Received ManageZone")
          // TODO: GESTIRE ZONA
          Behaviors.same
        }
      )
    })
