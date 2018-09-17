package ru.sber.cb.ap.gusli.actor.core.project

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Project._
import ru.sber.cb.ap.gusli.actor.core._

class EntityNotFoundSpec extends TestKit(ActorSystem("EntityNotFoundSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  val project: ActorRef = system.actorOf(Project(ProjectMetaDefault("project")), "project")

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Project" when {
    "it's have no entities" should {
      "when receive FindEntity(1) send back EntityNotFound(1)" in {
        project ! FindEntity(1)
        expectMsg(EntityNotFound(1))
      }
    }
  }
}

