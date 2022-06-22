package pcd.assignment03.distributed_programming.view

import pcd.assignment03.distributed_programming.model.{FireStation, Pluviometer, Zone}

trait SwingControlPanel:
  def updatePluviometer(pluviometer: Pluviometer): Unit
  def updateZone(zone: Zone): Unit
  def updateFirestation(fireStation: FireStation): Unit

object SwingControlPanel:

  def apply(view: View): SwingControlPanel = SwingControlPanelImpl(view)

  private class SwingControlPanelImpl(view: View) extends SwingControlPanel:

    override def updateFirestation(fireStation: FireStation): Unit = ???

    override def updatePluviometer(pluviometer: Pluviometer): Unit = ???

    override def updateZone(zone: Zone): Unit = ???
