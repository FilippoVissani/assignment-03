package pcd.assignment03.actor_programming.app

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import pcd.assignment03.actor_programming.control.actor.SimulationControllerActor.ControllerActorCommand.*
import pcd.assignment03.actor_programming.control.actor.{ChronometerActor, SimulationControllerActor}
import pcd.assignment03.actor_programming.entity.{Body, Vector2D}
import pcd.assignment03.actor_programming.entity.actor.BodyActor
import pcd.assignment03.actor_programming.entity.actor.BodyActor.BodyActorCommand
import pcd.assignment03.actor_programming.util.{Boundary, Point2D}
import pcd.assignment03.actor_programming.boundary.actor.ViewActor
import pcd.assignment03.actor_programming.boundary.View

import scala.language.postfixOps
import scala.util.Random

object RootActor:

  enum RootActorCommand:
    case Start
    case Stop

  export RootActorCommand.*

  def apply(): Behavior[RootActorCommand] =
    Behaviors.setup(ctx =>
      var bodyActors: List[ActorRef[BodyActorCommand]] = List()
      val random: Random = Random(System.currentTimeMillis())
      val bounds: Boundary = Boundary(-6.0, -6.0, 6.0, 6.0)
      for (i <- 1 to 500)
          bodyActors = ctx.spawn(BodyActor(Body(i, random, bounds)), s"body-actor-$i") :: bodyActors
          ctx.log.info(s"Spawned Body actor id $i")
      val chronometerActor = ctx.spawn(ChronometerActor(), s"Chronometer-actor")
      ctx.log.info(s"Spawned Chronometer actor")
      val controllerActor = ctx.spawn(SimulationControllerActor(bodyActors, chronometerActor = chronometerActor), s"Controller-actor")
      ctx.log.info(s"Spawned Controller actor")
      val viewActor = ctx.spawn(ViewActor(controllerActor, bounds, 800, 600), s"View-actor")
      ctx.log.info(s"Spawned View actor")
      controllerActor ! SetViewActor(viewActor)

      Behaviors.receive((context, msg) => msg match
        case Start => {
          context.log.debug("Received StartSimulation")
          controllerActor ! StartSimulation
          Behaviors.same
        }
        case Stop => {
          context.log.debug("Received Stop")
          controllerActor ! StopSimulation
          Behaviors.stopped
        }
    ))

  @main def main(): Unit =
    val system: ActorSystem[RootActorCommand] = ActorSystem(RootActor(), "root")
    //system ! Start

