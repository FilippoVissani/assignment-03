package pcd.assignment03.distributed_programming.view

import com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener
import pcd.assignment03.distributed_programming.model.ZoneState.*
import pcd.assignment03.distributed_programming.model.{FireStation, Pluviometer, Zone}

import java.awt.event.{WindowAdapter, WindowEvent}
import java.awt.{Dimension, Graphics2D, RenderingHints}
import javax.swing.{BorderFactory, SwingUtilities}
import scala.swing.BorderPanel.Position.{Center, North}
import scala.swing.{Action, BorderPanel, Button, FlowPanel, Frame, Panel}

trait SwingControlPanel:
  def updatePluviometer(pluviometer: Pluviometer): Unit
  def updateZone(zone: Zone): Unit
  def updateFirestation(fireStation: FireStation): Unit

object SwingControlPanel:

  def apply(view: View): SwingControlPanel = SwingControlPanelImpl(view)

  private class SwingControlPanelImpl(view: View) extends Frame with SwingControlPanel:
    val cityPanel: CityPanel = CityPanel(view.width, view.height)

    title = "Control Panel"
    //size = Dimension(view.width, view.height)
    resizable = false
    contents = new BorderPanel{
      layout(ButtonsPanel(view, cityPanel)) = North
      layout(cityPanel) = Center
    }
    visible = true

    addWindowListener(new WindowAdapter() {
      override def windowClosing(ev: WindowEvent): Unit =
        System.exit(-1)

      override def windowClosed(ev: WindowEvent): Unit =
        System.exit(-1)
    })

    override def updateFirestation(fireStation: FireStation): Unit =
      SwingUtilities.invokeLater(() => {
        cityPanel.updateFireStation(fireStation)
        repaint()
      })

    override def updatePluviometer(pluviometer: Pluviometer): Unit =
      SwingUtilities.invokeLater(() => {
        cityPanel.updatePluviometer(pluviometer)
        repaint()
      })

    override def updateZone(zone: Zone): Unit =
      SwingUtilities.invokeLater(() => {
        cityPanel.updateZone(zone)
        repaint()
      })

  end SwingControlPanelImpl
end SwingControlPanel

sealed class ButtonsPanel(view: View, simulationPanel: CityPanel) extends FlowPanel:
  val buttonManage: Button = new Button {
    text = "Manage Zone"
    action = new Action("Manage Zone"):
      override def apply(): Unit =
        view.manageZonePressed()
  }
  val buttonFix: Button = new Button{
    text = "Fix Zone"
    action = new Action("Fix Zone"):
      override def apply(): Unit =
        view.fixZonePressed()
  }
  contents += buttonManage
  contents += buttonFix
end ButtonsPanel

sealed class CityPanel(width: Int, height: Int) extends Panel:
  var fireStations: List[FireStation] = List()
  var zones: List[Zone] = List()
  var pluviometers: List[Pluviometer] = List()

  preferredSize = Dimension(width, height)

  override def paint(g: Graphics2D): Unit =
    val g2: Graphics2D = g
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    g2.drawRect(0, 0, width - 1, height - 1)
    g2.setColor(java.awt.Color.BLUE)
    zones.foreach(zone => {
      zone match
        case Zone(_, _, Ok) => g2.setColor(java.awt.Color.GREEN)
        case Zone(_, _, UnderManagement) => g2.setColor(java.awt.Color.YELLOW)
        case Zone(_, _, Alarm) => g2.setColor(java.awt.Color.RED)
      g2.fillRect(zone.bounds.x0, zone.bounds.y0, zone.bounds.width, zone.bounds.height)
      g2.setColor(java.awt.Color.BLACK)
      g2.drawString(zone.state.toString, zone.bounds.x0 + 5, zone.bounds.y0 + 15)
      g2.drawRect(zone.bounds.x0, zone.bounds.y0, zone.bounds.width, zone.bounds.height)
    })
    g2.setColor(java.awt.Color.BLACK)
    pluviometers.foreach(pluviometer => g2.fillOval(pluviometer.position.x, pluviometer.position.y, 10, 10))
    fireStations.foreach(fireStation => {
      g2.fillRect(fireStation.position.x, fireStation.position.y, 10, 10)
      g2.drawString(fireStation.state.toString, fireStation.position.x, fireStation.position.y + 20)
    })
  end paint

  def updatePluviometer(pluviometer: Pluviometer): Unit =
    this.pluviometers = pluviometer :: this.pluviometers.filter(x => x.position != pluviometer.position)
  def updateZone(zone: Zone): Unit =
    this.zones = zone :: this.zones.filter(x => x.id != zone.id)
  def updateFireStation(fireStation: FireStation): Unit =
    this.fireStations = fireStation :: this.fireStations.filter(x => x.zoneId != fireStation.zoneId)

end CityPanel
