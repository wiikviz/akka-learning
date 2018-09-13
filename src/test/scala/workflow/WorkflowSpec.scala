package workflow

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Workflow.{GetWorkflowMeta, WorkflowMetaResponse}
import ru.sber.cb.ap.gusli.actor.core._

class WorkflowSpec extends TestKit(ActorSystem("WorkflowSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {
  private val probe: TestProbe = TestProbe()

  val workflow: ActorRef = system.actorOf(Workflow(WorkflowMetaDefault("wf-1", "file.sql"), probe.ref), "workflow")

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A Workflow" when {
    "receive GetWorkflowMeta" should {
      workflow ! GetWorkflowMeta()
      "send back WorkflowMetaResponse" in {
        expectMsg(WorkflowMetaResponse("wf-1", "file.sql"))
      }
    }
  }
}

