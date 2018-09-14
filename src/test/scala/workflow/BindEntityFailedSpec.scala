package workflow

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Project.{EntityNotFound, FindEntity}
import ru.sber.cb.ap.gusli.actor.core.Workflow.{BindEntity, BindEntityFailedBecauseItNotExists}
import ru.sber.cb.ap.gusli.actor.core._
import scala.concurrent.duration._


class BindEntityFailedSpec extends TestKit(ActorSystem("BindEntityFailed")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  val projectProbe = TestProbe()
  val workflow = system.actorOf(Workflow(WorkflowMetaDefault("wf-1", "file.sql"), projectProbe.ref))

  "bind entity to workflow with project where it's entity not exists" must {
    workflow ! BindEntity(1)
    projectProbe.expectMsgAnyClassOf(classOf[FindEntity])
    projectProbe.reply(EntityNotFound(1))
    "return BindEntityFailedBecauseItNotExists" in {
      expectMsg(BindEntityFailedBecauseItNotExists(1))
    }
  }
}


