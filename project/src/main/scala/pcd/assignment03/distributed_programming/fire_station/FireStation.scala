package pcd.assignment03.distributed_programming.fire_station

import pcd.assignment03.distributed_programming.Point2D

enum FireStationState:
   case Free, Busy

trait FireStation:
   def zoneId: Int
   def position: Point2D
   def state: FireStationState

object FireStation:

   def apply(zoneId: Int, position: Point2D, state: FireStationState): FireStation =
      FireStationImpl(zoneId, position, state)

   private case class FireStationImpl(override val zoneId: Int,
                                      override val position: Point2D,
                                      override val state: FireStationState) extends FireStation
