package pcd.assignment03.actor_programming.controller

trait SimulationController:
  def timeStep: Double
  def virtualTime: Double
  def iterations: Long
  def incrementVirtualTime: SimulationController
  def incrementIterations: SimulationController
  
object SimulationController:
  def apply(virtualTime: Double = 0, iterations: Long = 0): SimulationController =
    SimulationControllerImpl(virtualTime, iterations)
  
  private class SimulationControllerImpl(
                                override val virtualTime: Double, 
                                override val iterations: Long,
                              ) extends SimulationController:
    override val timeStep: Double = 0.001

    override def incrementVirtualTime: SimulationController =
      SimulationController(virtualTime + timeStep, iterations)
    
    override def incrementIterations: SimulationController =
      SimulationController(virtualTime, iterations + 1)
