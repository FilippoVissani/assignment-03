package pcd.assignment03.actor_programming.app

import akka.actor.typed.ActorSystem

object SimulationAppGUI extends App:
  ActorSystem(RootActor(100, 100, true), "root")