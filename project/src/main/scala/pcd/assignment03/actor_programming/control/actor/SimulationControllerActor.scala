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
import pcd.assignment03.actor_programming.boundary.View
import pcd.assignment03.actor_programming.boundary.actor.ViewActor.ViewActorCommand
import pcd.assignment03.actor_programming.boundary.actor.ViewActor.ViewActorCommand.*

object SimulationControllerActor:

  enum ControllerActorCommand:
    case StartSimulation
    case StopSimulation
    case SetViewActor(viewActor: ActorRef[ViewActorCommand])
    // Risposte dei body
    case ResponseBody(body: Body)
    case PositionUpdated(body: Body)
    // Risposte del cronometro
    case ResponseDuration(duration: Long)

  export ControllerActorCommand.*

  def apply(bodyActors: List[ActorRef[BodyActorCommand]],
            viewActor: Option[ActorRef[ViewActorCommand]] = Option.empty,
            chronometerActor: ActorRef[ChronometerActorCommand],
            maxIterations: Long,
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
        bodyActors.foreach(bodyActor => bodyActor ! StopBodyActor)
        chronometerActor ! Duration(ctx.self)
        Behaviors.same
      }
      case SetViewActor(viewActor: ActorRef[ViewActorCommand]) => {
        ctx.log.debug("Received SetView")
        SimulationControllerActor(bodyActors, Option(viewActor), chronometerActor, maxIterations, bodies, controller)
      }
      case ResponseBody(body: Body) => {
        ctx.log.debug(s"Received ResponseBody ${body.id}")
        val tmpBodies = body :: bodies
        if tmpBodies.size == bodyActors.size then
          bodyActors.foreach(bodyActor => bodyActor ! UpdatePosition(tmpBodies, controller.timeStep, ctx.self))
          SimulationControllerActor(bodyActors, viewActor, chronometerActor, maxIterations, List(), controller)
        else
          SimulationControllerActor(bodyActors, viewActor, chronometerActor, maxIterations, tmpBodies, controller)
      }
      case PositionUpdated(body: Body) => {
        ctx.log.debug("Received PositionUpdated")
        val tmpBodies = body :: bodies
        if tmpBodies.size == bodyActors.size then
          if viewActor.isDefined then viewActor.get ! Display(tmpBodies.map(body => body.position), controller.virtualTime, controller.iterations)
          if controller.iterations < maxIterations then
            bodyActors.foreach(bodyActor => bodyActor ! RequestBody(ctx.self))
            SimulationControllerActor(bodyActors, viewActor, chronometerActor, maxIterations, List(), controller.incrementIterations.incrementVirtualTime)
          else
            ctx.log.debug("Max iterations reached")
            ctx.self ! StopSimulation
            Behaviors.same
        else
          SimulationControllerActor(bodyActors, viewActor, chronometerActor, maxIterations, tmpBodies, controller)
      }
      case ResponseDuration(duration: Long) => {
        ctx.log.debug("Received ResponseDuration")
        ctx.log.debug(s"ExecutionTime: $duration")
        Behaviors.stopped
      }
    )
