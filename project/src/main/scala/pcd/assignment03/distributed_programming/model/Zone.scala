package pcd.assignment03.distributed_programming.model

import pcd.assignment03.distributed_programming.model.Zone.ZoneState

trait Zone:
  def id: Int
  def bounds: Boundary
  def state: ZoneState
  def state_(newState: ZoneState): Zone

object Zone:

  enum ZoneState:
    case Ok, Alarm, UnderManagement

  export ZoneState.*

  def apply(id: Int, bounds: Boundary, state: ZoneState): Zone =
    ZoneImpl(id, bounds, state)

  private case class ZoneImpl(override val id: Int,
                              override val bounds: Boundary,
                              override val state: ZoneState) extends Zone:
    override def state_(newState: ZoneState): Zone =
      Zone(id, bounds, newState)
