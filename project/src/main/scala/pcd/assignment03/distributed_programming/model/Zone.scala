package pcd.assignment03.distributed_programming.model

trait Zone:
  def id: String
  def bounds: Boundary

object Zone:

  def apply(id: String, bounds: Boundary): Zone =
    ZoneImpl(id, bounds)

  private case class ZoneImpl(override val id: String,
                              override val bounds: Boundary) extends Zone
