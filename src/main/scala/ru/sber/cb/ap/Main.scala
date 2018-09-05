package ru.sber.cb.ap

import akka.actor.ActorSystem

import scala.io.StdIn
import domain._

object Main extends App{

  val system = ActorSystem("iot-system")

  try {
    // Create top level supervisor
    val rootName = "root"
    val root = system.actorOf(Category.props(rootName), rootName)
    StdIn.readLine()
  } finally {
    system.terminate()
  }
}
