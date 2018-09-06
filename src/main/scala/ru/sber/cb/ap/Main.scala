package ru.sber.cb.ap

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import ru.sber.cb.ap.domain.Category._
import ru.sber.cb.ap.domain._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

object Main extends App {
  val system = ActorSystem("ap-cli")
  implicit val timeout = domain.timeout

  try {
    val rootCategory = system.actorOf(Category(rootCategoryName), rootCategoryName)

    val child1 = Await.result(rootCategory ? AddSubcategory("cb1"), timeout.duration).asInstanceOf[ActorRef]
    val child2 = Await.result(rootCategory ? AddSubcategory("cb2"), timeout.duration).asInstanceOf[ActorRef]
    val subchild1 = Await.result(child1 ? AddSubcategory("ap"), timeout.duration).asInstanceOf[ActorRef]


    val catList = Await.result(rootCategory ? GetSubcategories, timeout.duration).asInstanceOf[List[ActorRef]]
    var i=0
    for (a <- catList ){
      a ! AddSubcategory(i.toString)
      i+=1
    }

    val v1 = Await.result(rootCategory ? GetSubcategories, timeout.duration).asInstanceOf[List[ActorRef]]
    println(s"result-1: ${v1.size} result=$v1")
    val v2 = Await.result(rootCategory ? GetSubcategories, timeout.duration).asInstanceOf[List[ActorRef]]
    println(s"result-2: ${v2.size} result=$v2")
    val v3 = Await.result(rootCategory ? GetSubcategories, timeout.duration).asInstanceOf[List[ActorRef]]
    println(s"result-3: ${v3.size} result=$v3")
    val v4 = Await.result(rootCategory ? GetSubcategories, timeout.duration).asInstanceOf[List[ActorRef]]
    println(s"result-4: ${v4.size} result=$v4")
    val v5 = Await.result(rootCategory ? GetSubcategories, timeout.duration).asInstanceOf[List[ActorRef]]
    println(s"result-5: ${v5.size} result=$v5")
    StdIn.readLine()
  } finally {
    system.terminate()
  }
}
