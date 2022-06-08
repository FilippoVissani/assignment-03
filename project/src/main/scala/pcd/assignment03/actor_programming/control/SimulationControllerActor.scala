package pcd.assignment03.actor_programming.control

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import pcd.assignment03.actor_programming.entity.actor.BodyActor.BodyActorCommand.RequestBody
import pcd.assignment03.actor_programming.entity.actor.BodyActor.BodyActorCommand
import pcd.assignment03.actor_programming.entity.logic.Body

object SimulationControllerActor:

  enum ControllerActorCommand:
    case StartSimulation
    case ResponseBody(body: Body)

  export ControllerActorCommand.*

  def apply(bodyActors: List[ActorRef[BodyActorCommand]]): Behavior[ControllerActorCommand] =
    Behaviors.receive((context, msg) => msg match
      case StartSimulation => {
        context.log.debug("Received Start")
        bodyActors.foreach(bodyActor => bodyActor ! RequestBody(context.self))
        Behaviors.same
      }
      case ResponseBody(body: Body) => {
        context.log.debug(s"Received ResponseBody ${body.id}")
        Behaviors.same
      }
      case _ => {
        context.log.debug("Received Stop")
        Behaviors.stopped
      }
    )
