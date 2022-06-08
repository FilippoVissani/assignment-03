package pcd.assignment03.actor_programming.app

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import pcd.assignment03.actor_programming.control.actor.SimulationControllerActor.ControllerActorCommand.StartSimulation
import pcd.assignment03.actor_programming.control.actor.{ChronometerActor, SimulationControllerActor}
import pcd.assignment03.actor_programming.entity.{Body, Boundary, Point2D, Vector2D}
import pcd.assignment03.actor_programming.entity.actor.BodyActor
import pcd.assignment03.actor_programming.entity.actor.BodyActor.BodyActorCommand

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
      for (i <- 1 to 5)
          bodyActors = ctx.spawn(BodyActor(Body(i)), s"body-actor-$i") :: bodyActors
          ctx.log.info(s"Spawned Body actor id $i")
      val chronometerActor = ctx.spawn(ChronometerActor(), s"Chronometer-actor")
      ctx.log.info(s"Spawned Chronometer actor")
      val controllerActor = ctx.spawn(SimulationControllerActor(bodyActors, chronometerActor), s"Controller-actor")
      ctx.log.info(s"Spawned Controller actor")

      Behaviors.receive((context, msg) => msg match
        case Start => {
          context.log.debug("Received StartSimulation")
          controllerActor ! StartSimulation
          Behaviors.same
        }
        case Stop => {
          context.log.debug("Received Stop")
          Behaviors.stopped
        }
    ))

  @main def main(): Unit =
    val system: ActorSystem[RootActorCommand] = ActorSystem(RootActor(), "root")
    system ! Start
