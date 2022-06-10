package pcd.assignment03.actor_programming.app

import akka.actor.typed.ActorSystem

object AppGUI extends App:
  ActorSystem(RootActor(1000, 1000, true), "root")