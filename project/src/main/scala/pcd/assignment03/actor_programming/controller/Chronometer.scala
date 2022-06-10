package pcd.assignment03.actor_programming.controller

/**
 * Trait used to represent a chronometer
 */
trait Chronometer:
  def startTime: Long
  def stopTime: Long
  /**
   * Start the chronometer
   */
  def start(): Chronometer

  /**
   * Stop the chronometer
   */
  def stop(): Chronometer

  /**
   * @return The time passed between start() and stop() calls or between start() and now if stop() was not called
   */
  def duration: Long

object Chronometer:
  def apply(startTime: Long = 0, stopTime: Long = 0): Chronometer = ChronometerImpl(startTime, stopTime)

  private class ChronometerImpl(override val startTime: Long,
                                override val stopTime: Long) extends Chronometer:

    override def start(): Chronometer = Chronometer(System.currentTimeMillis, stopTime)

    override def stop(): Chronometer = Chronometer(startTime, System.currentTimeMillis)

    override def duration: Long = stopTime - startTime
