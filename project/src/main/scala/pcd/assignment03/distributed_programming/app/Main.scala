package pcd.assignment03.distributed_programming.app

import akka.actor.typed.{ActorSystem, Behavior}
import com.typesafe.config.{Config, ConfigFactory}
import pcd.assignment03.distributed_programming.{Boundary, Point2D}
import pcd.assignment03.distributed_programming.zone.Zone
import pcd.assignment03.distributed_programming.zone.ZoneState.*
import pcd.assignment03.distributed_programming.fire_station.FireStation
import pcd.assignment03.distributed_programming.fire_station.FireStationState.*
import pcd.assignment03.distributed_programming.pluviometer.Pluviometer

import scala.util.Random

object Main:

  def generateZones(rows: Int, columns: Int, zoneSize: Boundary): List[Zone] =
    var zones: List[Zone] = List()
    var k: Int = 0
    for i <- 0 until rows do
      for j <- 0 until columns do
        zones = Zone(k,
          Boundary(i * zoneSize.width, j * zoneSize.height, (i * zoneSize.width + zoneSize.width) - 1, (j * zoneSize.height + zoneSize.height) - 1),
          Ok) :: zones
        k = k + 1
    zones

  def generateFireStations(zones: List[Zone]): List[FireStation] =
    var fireStations: List[FireStation] = List()
    val random: Random = Random(System.currentTimeMillis())
    zones.foreach(x => FireStation(x.id,
      Point2D(random.nextInt(x.bounds.width.toInt) + x.bounds.x0, random.nextInt(x.bounds.height.toInt) + x.bounds.y0),
      Free) :: fireStations)
    fireStations

  def generatePluviometers(zones: List[Zone]): List[Pluviometer] =
    var pluviometers: List[Pluviometer] = List()
    val random: Random = Random(System.currentTimeMillis())
    val circularZones = Iterator.continually(zones).flatten.take(6)
    circularZones.foreach(x => pluviometers = Pluviometer(x.id, Point2D(random.nextInt(x.bounds.width.toInt) + x.bounds.x0, random.nextInt(x.bounds.height.toInt) + x.bounds.y0)) :: pluviometers)
    pluviometers

  @main def main(width: Int, height: Int, rows: Int, columns: Int): Unit =
    val zones: List[Zone] = generateZones(rows, columns, Boundary(0, 0, width / (columns * rows), height / (columns * rows)))
    val fireStations: List[FireStation] = generateFireStations(zones)
    val pluviometers: List[Pluviometer] = generatePluviometers(zones)

  @main def startNewZone(): Unit = ???

  @main def startNewFireStation(): Unit = ???

  @main def startNewPluviometer(): Unit = ???

  @main def startNewControlPanel(): Unit = ???

  def startup[X](file: String = "pluviometer-cluster", port: Int)(root: => Behavior[X]): ActorSystem[X] =
    // Override the configuration of the port
    val config: Config = ConfigFactory
      .parseString(s"""akka.remote.artery.canonical.port=$port""")
      .withFallback(ConfigFactory.load(file))
    // Create an Akka system
    ActorSystem(root, "ClusterSystem", config)
