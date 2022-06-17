package pcd.assignment03.distributed_programming.view

import pcd.assignment03.distributed_programming.model.{FireStation, Pluviometer, Zone}

trait SwingControlPanel:
  def displayFireStation(fireStation: FireStation): Unit
  def displayPluviometer(pluviometer: Pluviometer): Unit
  def displayZone(zone: Zone): Unit

object SwingControlPanel:

  def apply(view: View): SwingControlPanel = SwingControlPanelImpl(view)

  private class SwingControlPanelImpl(view: View) extends SwingControlPanel:

    override def displayFireStation(fireStation: FireStation): Unit = ???

    override def displayPluviometer(pluviometer: Pluviometer): Unit = ???

    override def displayZone(zone: Zone): Unit = ???
