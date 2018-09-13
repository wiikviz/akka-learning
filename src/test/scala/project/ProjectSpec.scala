package project

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Project._
import ru.sber.cb.ap.gusli.actor.core._


class ProjectSpec extends TestKit(ActorSystem("ProjectSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {
  
  val proj: ActorRef = system.actorOf(Project(ProjectMetaDefault("project")), "project")
  
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }
  
  "A new project" when {
    "send GetProjectMeta" should {
       proj ! GetProjectMeta()
      "send back ProjectMetaResponse" in {
        expectMsg(ProjectMetaResponse("project"))
      }
    }
    "send GetCategoryRoot" should {
       proj ! GetCategoryRoot()
      "send back CategoryRoot" in {
        expectMsgAnyClassOf(classOf[CategoryRoot])
      }
    }
    "send GetEntityRoot" should {
      proj ! GetEntityRoot()
      "send back EntityRoot" in {
        expectMsgAnyClassOf(classOf[EntityRoot])
      }
    }
  }
}

