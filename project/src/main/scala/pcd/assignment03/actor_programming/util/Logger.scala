package pcd.assignment03.actor_programming.util

import java.io.{File, FileNotFoundException, FileOutputStream, PrintWriter}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

trait Logger:
  def logSimulationResult(nBodies: Int, nSteps: Long, executionTime: Long): Unit
  def logSimulationStarted(): Unit
  def logSimulationTerminated(): Unit

object Logger:

  def apply(): Logger = LoggerImpl()

  private class LoggerImpl extends Logger:

    override def logSimulationResult(nBodies: Int, nSteps: Long, executionTime: Long): Unit =
      log("--- TEST " + LocalDateTime.now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + " ---")
      log("[BODIES] => " + nBodies)
      log("[STEPS] => " + nSteps)
      log("[EXECUTION_TIME_MS] => " + executionTime)
      val milliseconds = executionTime.toInt % 1000
      val seconds = (executionTime / 1000).toInt % 60
      val minutes = ((executionTime / (1000 * 60)) % 60).toInt
      val hours = ((executionTime / (1000 * 60 * 60)) % 24).toInt
      log("[EXECUTION_TIME] => " + hours + "h " + minutes + "m " + seconds + "s " + milliseconds + "ms")

    override def logSimulationStarted(): Unit =
      log("SIMULATION STARTED " + LocalDateTime.now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))

    override def logSimulationTerminated(): Unit =
      log("SIMULATION TERMINATED " + LocalDateTime.now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
      log("")

    private def log(text: String): Unit =
      val out = new PrintWriter(new FileOutputStream(new File("simulation.log"), true))
      out.println(text)
      println(text)
      out.close()
