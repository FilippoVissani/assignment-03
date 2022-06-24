package pcd.assignment03.distributed_programming.model

import pcd.assignment03.distributed_programming.model.FireStationState.FireStationState

object FireStationState extends Enumeration:
   type FireStationState = Value
   val Free, Busy, Warned = Value

case class FireStation(zoneId: Int, 
                       position: Point2D, 
                       state: FireStationState):

   def state_(newState: FireStationState): FireStation =
      FireStation(zoneId, position, newState)
