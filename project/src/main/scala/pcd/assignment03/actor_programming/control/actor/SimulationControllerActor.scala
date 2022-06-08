package pcd.assignment03.actor_programming.control.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import pcd.assignment03.actor_programming.app.RootActor.RootActorCommand
import pcd.assignment03.actor_programming.control.actor.ChronometerActor.ChronometerActorCommand
import pcd.assignment03.actor_programming.control.{Chronometer, SimulationController}
import pcd.assignment03.actor_programming.entity.Body
import pcd.assignment03.actor_programming.entity.actor.BodyActor.BodyActorCommand
import pcd.assignment03.actor_programming.entity.actor.BodyActor.BodyActorCommand.*
import pcd.assignment03.actor_programming.control.actor.ChronometerActor.ChronometerActorCommand.*

object SimulationControllerActor:

  enum ControllerActorCommand:
    case StartSimulation
    case StopSimulation
    // Risposte dei body
    case ResponseBody(body: Body)
    case PositionUpdated(body: Body)
    // Risposte del cronometro
    case ResponseDuration(duration: Long)

  export ControllerActorCommand.*

  def apply(bodyActors: List[ActorRef[BodyActorCommand]],
            chronometerActor: ActorRef[ChronometerActorCommand],
            bodies: List[Body] = List(),
            controller: SimulationController = SimulationController()
           ): Behavior[ControllerActorCommand] =
    Behaviors.receive((ctx, msg) => msg match
      case StartSimulation => {
        ctx.log.debug("Received StartSimulation")
        chronometerActor ! Start
        bodyActors.foreach(bodyActor => bodyActor ! RequestBody(ctx.self))
        Behaviors.same
      }
      case StopSimulation => {
        ctx.log.debug("Received StopSimulation")
        chronometerActor ! Stop
        chronometerActor ! Duration(ctx.self)
        Behaviors.same
      }
      case ResponseBody(body: Body) => {
        ctx.log.debug(s"Received ResponseBody ${body.id}")
        val tmpBodies = body :: bodies
        if tmpBodies.size == bodyActors.size then
          bodyActors.foreach(bodyActor => bodyActor ! UpdatePosition(tmpBodies, controller.timeStep, ctx.self))
          SimulationControllerActor(bodyActors, chronometerActor, List(), controller)
        else
          SimulationControllerActor(bodyActors, chronometerActor, tmpBodies, controller)
      }
      case PositionUpdated(body: Body) => {
        ctx.log.debug("Received PositionUpdated")
        val tmpBodies = body :: bodies
        if tmpBodies.size == bodyActors.size then
        // manda alla view le posizioni
          SimulationControllerActor(bodyActors, chronometerActor, List(), controller.incrementIterations.incrementVirtualTime)
        else
          SimulationControllerActor(bodyActors, chronometerActor, tmpBodies, controller)
      }
      case ResponseDuration(duration: Long) => {
        ctx.log.debug("Received ResponseDuration")
        ctx.log.info(s"ExecutionTime: $duration")
        Behaviors.same
      }
      case _ => {
        ctx.log.debug("Received Stop")
        Behaviors.stopped
      }
    )
