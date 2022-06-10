package pcd.assignment03.actor_programming.app

import akka.actor.typed.ActorSystem
import pcd.assignment03.actor_programming.app.RootActor.RootActorCommand.Start

object App extends App:
  val system = ActorSystem(RootActor(1000, 1000, false), "root")
  system ! Start
