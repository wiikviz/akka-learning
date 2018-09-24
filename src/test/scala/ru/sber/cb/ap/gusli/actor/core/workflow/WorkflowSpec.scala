package ru.sber.cb.ap.gusli.actor.core.workflow

import akka.actor.ActorRef
import akka.testkit.{TestKit, TestProbe}
import ru.sber.cb.ap.gusli.actor.core.Workflow.{GetWorkflowMeta, WorkflowMetaResponse}
import ru.sber.cb.ap.gusli.actor.core._

class WorkflowSpec extends ActorBaseTest("WorkflowSpec") {
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A Workflow" when {
    val project = TestProbe()
    val metaDefault = WorkflowMetaDefault("wf-1", Map("file" -> "select 1"), Map.empty)
    val workflow: ActorRef = system.actorOf(Workflow(metaDefault, project.ref))
    "receive GetWorkflowMeta" should {
      "send back WorkflowMetaResponse" in {
        workflow ! GetWorkflowMeta()
        expectMsg(WorkflowMetaResponse(metaDefault))
      }
    }
  }
}

