package pcd.assignment03.distributed_programming.view

import akka.actor.typed.ActorRef
import pcd.assignment03.distributed_programming.actors.ViewActor.ViewActorCommand
import pcd.assignment03.distributed_programming.model.{FireStation, Pluviometer, Zone}

trait View:
  def width: Int
  def height: Int
  def displayFireStation(fireStation: FireStation): Unit
  def displayPluviometer(pluviometer: Pluviometer): Unit
  def displayZone(zone: Zone): Unit
  def manageZonePressed(): Unit
  def fixZonePressed(): Unit

object View:
  def apply(width: Int, height: Int, viewActor: ActorRef[ViewActorCommand]): View =
    ViewImpl(width, height, viewActor)

  /**
   * Implementation of View trait
   */
  private class ViewImpl(override val width: Int,
                         override val height: Int,
                         val viewActor: ActorRef[ViewActorCommand]) extends View:
    val frame: SwingControlPanel = SwingControlPanel(this)

    override def displayFireStation(fireStation: FireStation): Unit =
      frame.displayFireStation(fireStation)

    override def displayPluviometer(pluviometer: Pluviometer): Unit =
      frame.displayPluviometer(pluviometer: Pluviometer)

    override def displayZone(zone: Zone): Unit =
      frame.displayZone(zone: Zone)

    override def fixZonePressed(): Unit = ???
      //viewActor ! FixZone

    override def manageZonePressed(): Unit = ???
      //viewActor ! ManageZone