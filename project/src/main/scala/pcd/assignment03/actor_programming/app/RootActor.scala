package pcd.assignment03.actor_programming.app

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import pcd.assignment03.actor_programming.control.SimulationControllerActor
import pcd.assignment03.actor_programming.control.SimulationControllerActor.ControllerActorCommand.StartSimulation
import pcd.assignment03.actor_programming.entity.actor.BodyActor
import pcd.assignment03.actor_programming.entity.actor.BodyActor.BodyActorCommand
import pcd.assignment03.actor_programming.entity.logic.{Body, Boundary, Point2D, Vector2D}

import scala.language.postfixOps
import scala.util.Random

object RootActor:

  enum RootActorCommand:
    case Start
    case Stop

  export RootActorCommand.*

  def apply(): Behavior[RootActorCommand] =
    val bounds: Boundary = Boundary(-6.0, -6.0, 6.0, 6.0)
    val rand: Random = Random(System.currentTimeMillis())
    Behaviors.setup(ctx =>
      var bodies: List[ActorRef[BodyActorCommand]] = List()
      for (i <- 1 to 1000)
        val x: Double = bounds.x0 * 0.25 + rand.nextDouble() * (bounds.x1 - bounds.x0) * 0.25
        val y: Double = bounds.y0 * 0.25 + rand.nextDouble() * (bounds.y1 - bounds.y0) * 0.25
        val body: Body = Body(i, Point2D(x, y), Vector2D(0, 0), 10, bounds)
          bodies = ctx.spawn(BodyActor(body), s"body-actor-$i") :: bodies
          ctx.log.info(s"Spawned body actor id $i")
      val controller = ctx.spawn(SimulationControllerActor(bodies), s"controller-actor")
      ctx.log.info(s"Spawned controller actor")

      Behaviors.receive((context, msg) => msg match
        case Start => {
          context.log.debug("Received StartSimulation")
          controller ! StartSimulation
          Behaviors.same
        }
        case _ => {
          context.log.debug("Received Stop")
          Behaviors.stopped
        }
    ))

  @main def main(): Unit =
    val system: ActorSystem[RootActorCommand] = ActorSystem(RootActor(), "root")
    system ! Start
