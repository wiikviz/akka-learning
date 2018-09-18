package ru.sber.cb.ap.gusli.actor.core.workflow

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Workflow.{GetWorkflowMeta, WorkflowMetaResponse}
import ru.sber.cb.ap.gusli.actor.core._

class WorkflowSpec extends TestKit(ActorSystem("WorkflowSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A Workflow" when {
    val workflow: ActorRef = system.actorOf(Workflow(WorkflowMetaDefault("wf-1", Map("file" -> "select 1"), Map.empty), TestProbe().ref))
    "receive GetWorkflowMeta" should {
      workflow ! GetWorkflowMeta()
      "send back WorkflowMetaResponse" in {
        expectMsg(WorkflowMetaResponse(WorkflowMetaDefault("wf-1", Map("file" -> "select 1"), Map.empty)))
      }
    }
  }
}

