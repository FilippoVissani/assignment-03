package pcd.assignment03.distributed_programming.model

import pcd.assignment03.distributed_programming.model.Zone.ZoneState

trait Zone:
  def id: String
  def bounds: Boundary
  def state: ZoneState
  def threshold: Float
  def state_(newState: ZoneState): Zone

object Zone:

  enum ZoneState:
    case Ok, Alarm, UnderManagement
  
  export ZoneState.*

  def apply(id: String, bounds: Boundary, state: ZoneState, threshold: Float): Zone =
    ZoneImpl(id, bounds, state, threshold)

  private case class ZoneImpl(override val id: String,
                              override val bounds: Boundary,
                              override val state: ZoneState,
                              override val threshold: Float) extends Zone:
    override def state_(newState: ZoneState): Zone =
      Zone(id, bounds, newState, threshold)
