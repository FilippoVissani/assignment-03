package pcd.assignment03.actor_programming.app

import akka.actor.typed.ActorSystem
import pcd.assignment03.actor_programming.app.RootActor.RootActorCommand.Start

object SimulationApp extends App:
  val system = ActorSystem(RootActor(args(0).toInt, args(1).toLong, false), "root")
  system ! Start
