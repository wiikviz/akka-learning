package workflow

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Project.{EntityNotFound, FindEntity}
import ru.sber.cb.ap.gusli.actor.core.Workflow.{BindEntity, BindEntityFailedBecauseItNotExists}
import ru.sber.cb.ap.gusli.actor.core._

class BindWorkflowSpec extends TestKit(ActorSystem("BindWorkflowSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "bind entity to workflow with project where it's entity not exists" must {
    val project = system.actorOf(Props(new Actor {
      def receive = {
        case FindEntity(entityId, _) => sender() ! EntityNotFound(entityId)
      }
    }))

    val workflow: ActorRef = system.actorOf(Workflow(WorkflowMetaDefault("wf-1", "file.sql"), project))
    "return BindEntityFailedBecauseItNotExists" in {
      workflow ! BindEntity(1)
      expectMsg(BindEntityFailedBecauseItNotExists(1))
    }
  }
}

