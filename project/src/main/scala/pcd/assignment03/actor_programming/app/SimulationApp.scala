package pcd.assignment03.actor_programming.app

import akka.actor.typed.ActorSystem
import pcd.assignment03.actor_programming.app.RootActor.RootActorCommand.Start

object SimulationApp:
  @main def main(bodies: Int, iterations: Long): Unit =
    val system = ActorSystem(RootActor(bodies, iterations, false), "root")
    system ! Start
