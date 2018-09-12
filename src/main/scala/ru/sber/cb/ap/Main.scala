package ru.sber.cb.ap

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import ru.sber.cb.ap.gusli.actor.core.Category._
import ru.sber.cb.ap.gusli.actor.core.Project._
import ru.sber.cb.ap.gusli.actor.core._

import scala.concurrent.duration._
import scala.io.StdIn

object Main extends App {
  val system = ActorSystem("ap-cli")
  try {
    implicit val timeout = Timeout(5 seconds)
    import system.dispatcher
    val projectA: ActorRef = system.actorOf(Project(ProjectMetaDefault(name = "projectA")), "projectA")


    (projectA ? GetCategoryRoot()).map(x => x.asInstanceOf[CategoryRoot].root ! AddWorkflow(WorkflowMetaDefault("wf-1", "1.sql")))


    println("Press enter to finish application...")
    StdIn.readLine()
  } finally {
    system.terminate()
  }
}
