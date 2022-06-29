package pcd.assignment03.distributed_programming.app

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import com.typesafe.config.{Config, ConfigFactory}
import pcd.assignment03.distributed_programming.actors.{FireStationActor, FireStationGuardianActor, PluviometerActor, PluviometerGuardianActor, ViewActor, ViewGuardianActor}
import pcd.assignment03.distributed_programming.model.{Boundary, FireStation, Pluviometer, Point2D, Zone}
import pcd.assignment03.distributed_programming.model.ZoneState.*
import pcd.assignment03.distributed_programming.model.FireStationState.*

import scala.util.Random

object Main:

  def generateZones(rows: Int, columns: Int, zoneSize: Boundary): List[Zone] =
    var zones: List[Zone] = List()
    var k: Int = 0
    for i <- 0 until columns do
      for j <- 0 until rows do
        zones = Zone(k,
          Boundary(i * zoneSize.width, j * zoneSize.height, (i * zoneSize.width) + zoneSize.width - 1, (j * zoneSize.height) + zoneSize.height - 1),
          Ok) :: zones
        k = k + 1
    zones.reverse

  def generateFireStations(zones: List[Zone]): List[FireStation] =
    var fireStations: List[FireStation] = List()
    val random: Random = Random(System.currentTimeMillis())
    zones.foreach(x => fireStations = FireStation(x.id,
      Point2D(random.nextInt(x.bounds.width - 100) + x.bounds.x0 + 50, random.nextInt(x.bounds.height - 100) + x.bounds.y0 + 50),
      Free) :: fireStations)
    fireStations.reverse

  def generatePluviometers(zones: List[Zone]): List[Pluviometer] =
    var pluviometers: List[Pluviometer] = List()
    val random: Random = Random(System.currentTimeMillis())
    val circularZones = Iterator.continually(zones).flatten.take(6)
    var pluviometerId: Int = 0
    circularZones.foreach(x => {
      pluviometers = Pluviometer(x.id,
        Point2D(random.nextInt(x.bounds.width - 100) + x.bounds.x0 + 50, random.nextInt(x.bounds.height - 100) + x.bounds.y0 + 50),
        5) :: pluviometers
      pluviometerId = pluviometerId + 1
    })
    pluviometers

  @main def main(): Unit =
    val rows = 2
    val columns = 2
    val width = columns * 200
    val height = rows * 200
    var port: Int = 2551
    val zones: List[Zone] = generateZones(rows, columns, Boundary(0, 0, width / columns, height / rows))
    val fireStations: List[FireStation] = generateFireStations(zones)
    val pluviometers: List[Pluviometer] = generatePluviometers(zones)
    fireStations.foreach(f => {
      startup(port = port)(FireStationGuardianActor(f, zones(f.zoneId)))
      port = port + 1
    })
    pluviometers.foreach(p => {
      startup(port = port)(PluviometerGuardianActor(p))
      port = port + 1
    })
    startup(port = 1200)(ViewGuardianActor(0, width, height))
    startup(port = 1201)(ViewGuardianActor(1, width, height))

  def startup[X](file: String = "cluster", port: Int)(root: => Behavior[X]): ActorSystem[X] =
    // Override the configuration of the port
    val config: Config = ConfigFactory
      .parseString(s"""akka.remote.artery.canonical.port=$port""")
      .withFallback(ConfigFactory.load(file))
    // Create an Akka system
    ActorSystem(root, "ClusterSystem", config)
