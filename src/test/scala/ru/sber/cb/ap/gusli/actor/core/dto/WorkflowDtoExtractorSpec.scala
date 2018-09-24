package ru.sber.cb.ap.gusli.actor.core.dto

import akka.actor.ActorRef
import akka.testkit.TestProbe
import ru.sber.cb.ap.gusli.actor.core.Entity.{EntityMetaResponse, GetEntityMeta}
import ru.sber.cb.ap.gusli.actor.core.Project.{EntityFound, FindEntity}
import ru.sber.cb.ap.gusli.actor.core.Workflow.{BindEntity, BindEntitySuccessful}
import ru.sber.cb.ap.gusli.actor.core._
import ru.sber.cb.ap.gusli.actor.core.extractor.WorkflowDtoExtractor
import ru.sber.cb.ap.gusli.actor.core.extractor.WorkflowDtoExtractor.WorkflowExtracted

class WorkflowDtoExtractorSpec extends ActorBaseTest("WorkflowDtoExtractorSpec") {
  private val receiver = TestProbe()
  private val project = TestProbe()
  private val metaDefault = WorkflowMetaDefault("wf-1", Map("file" -> "select 1"))
  private val workflow: ActorRef = system.actorOf(Workflow(metaDefault, project.ref), "workflow")

  "A WorkflowDtoExtractor" when {
    "receive Extract(workflow)" should {
      "return WorkflowDto with no entities" in {
        system.actorOf(WorkflowDtoExtractor(workflow, receiver.ref))
        receiver.expectMsg(WorkflowExtracted(WorkflowDto("wf-1", Map("file" -> "select 1"), entities = Set.empty)))
      }

      "return WorkflowDto with entityId=3" in {
        val entity3 = TestProbe()
        workflow ! BindEntity(3)
        project.expectMsg(FindEntity(3))
        project.reply(EntityFound(EntityMetaDefault(3, "entity3", "/entity3", None), entity3.ref))
        expectMsg(BindEntitySuccessful(3))
        system.actorOf(WorkflowDtoExtractor(workflow, receiver.ref))
        entity3.expectMsg(GetEntityMeta())
        entity3.reply(EntityMetaResponse(EntityMetaDefault(3, "entity3", "/entity3", None)))
        receiver.expectMsg(WorkflowExtracted(WorkflowDto("wf-1", Map("file" -> "select 1"), entities = Set(3))))
      }
    }
  }
}