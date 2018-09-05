package ru.sber.cb.ap

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import ru.sber.cb.ap.domain._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

object Main extends App {

  val system = ActorSystem("ap-cli")

  try {
    import domain.Category._
    val root = system.actorOf(Category(rootCategoryName), rootCategoryName)
    implicit val timeout = Timeout(5 seconds)
    val child1 = Await.result(root ? AddSubcategory("cb"), timeout.duration).asInstanceOf[ActorRef]
    val subchild = Await.result(child1 ? AddSubcategory("ap"), timeout.duration).asInstanceOf[ActorRef]
    println(child1)
    StdIn.readLine()
  } finally {
    system.terminate()
  }
}
