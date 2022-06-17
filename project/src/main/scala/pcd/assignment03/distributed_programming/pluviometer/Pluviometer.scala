package pcd.assignment03.distributed_programming.pluviometer

import pcd.assignment03.distributed_programming.Point2D

trait Pluviometer:
  def zoneId: Int
  def position: Point2D

object Pluviometer:

  def apply(zoneId: Int, position: Point2D): Pluviometer =
    PluviometerImpl(zoneId, position)

  private case class PluviometerImpl(override val zoneId: Int,
                                     override val position: Point2D) extends Pluviometer
