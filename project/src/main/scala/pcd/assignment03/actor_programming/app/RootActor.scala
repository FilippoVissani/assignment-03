package pcd.assignment03.actor_programming.app

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Terminated}
import pcd.assignment03.actor_programming.view.View
import pcd.assignment03.actor_programming.view.actor.ViewActor
import pcd.assignment03.actor_programming.controller.actor.SimulationControllerActor.ControllerActorCommand
import pcd.assignment03.actor_programming.controller.actor.SimulationControllerActor.ControllerActorCommand.*
import pcd.assignment03.actor_programming.controller.actor.{ChronometerActor, SimulationControllerActor}
import pcd.assignment03.actor_programming.model.actor.BodyActor
import pcd.assignment03.actor_programming.model.actor.BodyActor.BodyActorCommand
import pcd.assignment03.actor_programming.model.{Body, Vector2D}
import pcd.assignment03.actor_programming.util.{Boundary, Point2D}

import scala.language.postfixOps
import scala.util.Random

object RootActor:

  enum RootActorCommand:
    case Start

  export RootActorCommand.*

  def apply(bodiesNumber: Int, maxIterations: Long, withView: Boolean): Behavior[RootActorCommand] =
    Behaviors.setup[RootActorCommand](ctx =>
      var bodyActors: List[ActorRef[BodyActorCommand]] = List()
      val random: Random = Random(System.currentTimeMillis())
      val bounds: Boundary = Boundary(-6.0, -6.0, 6.0, 6.0)
      for (i <- 1 to bodiesNumber)
          bodyActors = ctx.spawn(BodyActor(Body(i, random, bounds)), s"body-actor-$i") :: bodyActors
          ctx.log.info(s"Spawned Body actor id $i")
      val chronometerActor = ctx.spawn(ChronometerActor(), s"Chronometer-actor")
      ctx.log.info(s"Spawned Chronometer actor")
      val controllerActor: ActorRef[ControllerActorCommand] = ctx.spawn(SimulationControllerActor(bodyActors, maxIterations = maxIterations,chronometerActor = chronometerActor), s"Controller-actor")
      ctx.log.info(s"Spawned Controller actor")
      if withView then
        val viewActor = ctx.spawn(ViewActor(controllerActor, bounds, 800, 600), s"View-actor")
        ctx.log.info(s"Spawned View actor")
        controllerActor ! SetViewActor(viewActor)
      else
        ctx.watch(controllerActor)

      Behaviors.receive[RootActorCommand]((ctx, msg) => msg match
        case Start => {
          ctx.log.debug("Received StartSimulation")
          controllerActor ! StartSimulation
          Behaviors.same
        }
        case _ => {
          ctx.log.debug("Received Stop")
          controllerActor ! StopSimulation
          Behaviors.stopped
        }
      ).receiveSignal { case (ctx, Terminated(_)) =>
        ctx.log.debug("Stopping actor system")
        Behaviors.stopped
      }
    )