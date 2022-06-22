package pcd.assignment03.distributed_programming.view

import akka.actor.typed.ActorRef
import akka.actor.typed.receptionist.Receptionist
import pcd.assignment03.distributed_programming.model.{FireStation, Pluviometer, Zone}
import pcd.assignment03.distributed_programming.actors.{FixZone, ManageZone, ViewActorCommand}

trait View:
  def width: Int
  def height: Int
  def zoneId: Int
  def updatePluviometer(pluviometer: Pluviometer): Unit
  def updateZone(zone: Zone): Unit
  def updateFireStation(fireStation: FireStation): Unit
  def manageZonePressed(): Unit
  def fixZonePressed(): Unit

object View:
  def apply(width: Int, height: Int, zoneId: Int, viewActor: ActorRef[ViewActorCommand | Receptionist.Listing]): View =
    ViewImpl(width, height, zoneId, viewActor)

  /**
   * Implementation of View trait
   */
  private class ViewImpl(override val width: Int,
                         override val height: Int,
                         override val zoneId: Int,
                         val viewActor: ActorRef[ViewActorCommand | Receptionist.Listing]) extends View:
    val frame: SwingControlPanel = SwingControlPanel(this)

    override def updateZone(zone: Zone): Unit =
      frame.updateZone(zone)

    override def updatePluviometer(pluviometer: Pluviometer): Unit =
      frame.updatePluviometer(pluviometer)

    override def updateFireStation(fireStation: FireStation): Unit =
      frame.updateFirestation(fireStation)

    override def fixZonePressed(): Unit =
      viewActor ! FixZone

    override def manageZonePressed(): Unit =
      viewActor ! ManageZone