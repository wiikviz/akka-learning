package ru.sber.cb.ap.gusli.actor.core.workflow

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Project.{EntityFound, FindEntity}
import ru.sber.cb.ap.gusli.actor.core.Workflow.{BindEntity, BindEntitySuccessful, GetWorkflowMeta, WorkflowMetaResponse}
import ru.sber.cb.ap.gusli.actor.core._

class WorkflowSpec extends TestKit(ActorSystem("WorkflowSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A Workflow" when {
    val project = TestProbe()
//    val entity3 = TestProbe()
    val metaDefault = WorkflowMetaDefault("wf-1", Map("file" -> "select 1"), Map.empty)
    val workflow: ActorRef = system.actorOf(Workflow(metaDefault, project.ref))
    "receive GetWorkflowMeta" should {
      "send back WorkflowMetaResponse" in {
        workflow ! GetWorkflowMeta()
        expectMsg(WorkflowMetaResponse(metaDefault))
      }

//      "receive meta with new entity id after add entities" in {
//        workflow ! BindEntity(3)
//        project.expectMsg(FindEntity(3))
//        project.reply(EntityFound(EntityMetaDefault(3, "entity3", "/entity3", None), entity3.ref))
//        expectMsg(BindEntitySuccessful(3))
//        workflow ! GetWorkflowMeta()
//        expectMsg(WorkflowMetaDefault("wf-1", Map("file" -> "select 1"), Map.empty, entities = Set(1, 2, 3)))
//      }
    }
  }
}

