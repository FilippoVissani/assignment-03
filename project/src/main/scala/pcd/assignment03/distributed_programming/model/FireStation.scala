package pcd.assignment03.distributed_programming.model

import pcd.assignment03.distributed_programming.model.FireStation.FireStationState

trait FireStation:
   def zoneId: String
   def position: Point2D
   def state: FireStationState
   def state_(newState: FireStationState): FireStation

object FireStation:

   enum FireStationState:
      case Free, Busy, Warned
      
   export FireStationState.*

   def apply(zoneId: String, position: Point2D, state: FireStationState): FireStation =
      FireStationImpl(zoneId, position, state)

   private case class FireStationImpl(override val zoneId: String,
                                      override val position: Point2D,
                                      override val state: FireStationState) extends FireStation:

      override def state_(newState: FireStationState): FireStation =
         FireStation(zoneId, position, newState)
