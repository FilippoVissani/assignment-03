package pcd.assignment03.actor_programming.view

import com.sun.java.accessibility.util.AWTEventMonitor.{addKeyListener, addWindowListener}
import pcd.assignment03.actor_programming.util.{Boundary, Point2D}

import java.awt.event.{KeyEvent, KeyListener, WindowAdapter, WindowEvent}
import java.awt.{BorderLayout, Dimension, Graphics2D, RenderingHints}
import javax.swing.SwingUtilities
import scala.collection.mutable
import scala.language.postfixOps
import scala.swing.*
import scala.swing.BorderPanel.Position.*

class SwingSimulationGUI(graphicalView: View, width: Int, height: Int) extends Frame:
  val simulationPanel: SimulationPanel = SimulationPanel(width, height, graphicalView.bounds)

  addWindowListener(new WindowAdapter() {
    override def windowClosing(ev: WindowEvent): Unit =
      System.exit(-1)

    override def windowClosed(ev: WindowEvent): Unit =
      System.exit(-1)
  })

  title = "Bodies Simulation"
  size = Dimension(width, height)
  resizable = false
  contents = new BorderPanel{
    layout(ButtonsPanel(graphicalView, simulationPanel)) = North
    layout(simulationPanel) = Center
  }
  visible = true

  def display(bodiesPositions: List[Point2D],
              virtualTime: Double,
              currentIteration: Long): Unit =
    SwingUtilities.invokeLater(() => {
      simulationPanel.display(bodiesPositions, virtualTime, currentIteration)
      repaint()
    })
end SwingSimulationGUI

sealed class ButtonsPanel(graphicalView: View, simulationPanel: SimulationPanel) extends FlowPanel:
  val buttonStart: Button = new Button {
    text = "Start"
    action = new Action("start"):
      override def apply(): Unit =
        enabled = false
        buttonStop.enabled = true
        graphicalView.startSimulation()
  }
  val buttonStop: Button = new Button{
    text = "Stop"
    enabled = false
    action = new Action("stop"):
      override def apply(): Unit =
        enabled = false
        graphicalView.stopSimulation()
  }
  val buttonPlus: Button = new Button{
    text = "+"
    enabled = false
    action = new Action("+"):
      override def apply(): Unit =
        simulationPanel.zoomIn()
  }
  val buttonMinus: Button = new Button{
    text = "-"
    enabled = false
    action = new Action("-"):
      override def apply(): Unit =
        simulationPanel.zoomOut()
  }

  contents += buttonStart
  contents += buttonStop
  contents += buttonPlus
  contents += buttonMinus

end ButtonsPanel

sealed class SimulationPanel(width: Int, height: Int, bounds: Boundary) extends Panel:
  var bodiesPositions: List[Point2D] = List()
  var currentIteration: Long = 0
  var virtualTime: Double = 0
  var scale: Double = 1
  val dx: Long = width / 2 - 20
  val dy: Long = height / 2 - 20

  preferredSize = Dimension(width, height)

  override def paint(g: Graphics2D): Unit =
    if bodiesPositions.nonEmpty then
      val g2: Graphics2D = g
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
      g2.clearRect(0, 0, width, height)

      val x0 = xCoord(bounds.x0)
      val y0 = yCoord(bounds.y0)

      val wd = xCoord(bounds.x1) - x0
      val ht = y0 - yCoord(bounds.y1)

      g2.drawRect(x0, y0 - ht, wd, ht)

      bodiesPositions.foreach(position => {
        var radius: Int = (10 * scale).toInt
        if (radius < 1) {
          radius = 1
        }
        g2.drawOval(xCoord(position.x), yCoord(position.y), radius, radius)
      })
      val time: String = String.format("%.2f", virtualTime)
      g2.drawString("Bodies: " + bodiesPositions.size + " - vt: " + time + " - nIter: " + currentIteration, 2, 20)
  end paint

  private def xCoord(x: Double): Int = (dx + x * dx * scale).toInt

  private def yCoord(y: Double): Int = (dy - y * dy * scale).toInt

  def display(bodiesPositions: List[Point2D], vt: Double, iter: Long): Unit =
    this.bodiesPositions = bodiesPositions
    this.virtualTime = vt
    this.currentIteration = iter

  def zoomIn(): Unit =
    scale *= 1.1

  def zoomOut(): Unit =
    scale *= 0.9

end SimulationPanel
