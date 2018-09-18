package ru.sber.cb.ap.gusli.actor.core.project

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Project._
import ru.sber.cb.ap.gusli.actor.core._


class ProjectSpec extends TestKit(ActorSystem("ProjectSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {
  
  val project: ActorRef = system.actorOf(Project(ProjectMetaDefault("project")), "project")

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A new project" when {
    "send GetProjectMeta" should {
       project ! GetProjectMeta()
      "send back ProjectMetaResponse" in {
        expectMsg(ProjectMetaResponse(ProjectMetaDefault("project")))
      }
    }

    "send GetCategoryRoot" should {
       project ! GetCategoryRoot()
      "send back CategoryRoot" in {
        expectMsgAnyClassOf(classOf[CategoryRoot])
      }
    }

    "send GetEntityRoot" should {
      project ! GetEntityRoot()
      "send back EntityRoot" in {
        expectMsgAnyClassOf(classOf[EntityRoot])
      }
    }
  
    "send FindEntity(-2)" should {
      project ! FindEntity(-2)
      "send back EntityNotFound(-2)" in {
        expectMsg(EntityNotFound(-2))
      }
    }
  }
}

