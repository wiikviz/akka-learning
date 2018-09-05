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
    catList.zipWithIndex.foreach( (t)=> t._1 ! AddSubcategory(t._2.toString))

    println("result="+catList)
    println()
    println(Await.result(rootCategory ? GetSubcategories, timeout.duration))
    StdIn.readLine()
  } finally {
    system.terminate()
  }
}
