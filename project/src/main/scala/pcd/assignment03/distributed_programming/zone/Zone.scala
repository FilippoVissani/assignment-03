package pcd.assignment03.distributed_programming.zone

import pcd.assignment03.distributed_programming.Boundary

enum ZoneState:
  case Ok, Alarm, UnderManagement

trait Zone:
  def id: Int
  def bounds: Boundary
  def state: ZoneState

object Zone:

  def apply(id: Int, bounds: Boundary, state: ZoneState): Zone = ZoneImpl(id, bounds, state)

  private case class ZoneImpl(override val id: Int,
                              override val bounds: Boundary,
                              override val state: ZoneState) extends Zone
