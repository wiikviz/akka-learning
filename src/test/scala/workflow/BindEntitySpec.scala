package workflow

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Project.{EntityFound, EntityNotFound, FindEntity}
import ru.sber.cb.ap.gusli.actor.core.Workflow.{BindEntity, BindEntityFailedBecauseItNotExists, BindEntitySuccessful}
import ru.sber.cb.ap.gusli.actor.core._


class BindEntitySpec extends TestKit(ActorSystem("BindEntity")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {
  val projectProbe = TestProbe()
  val workflow = system.actorOf(Workflow(WorkflowMetaDefault("wf-1", "file.sql"), projectProbe.ref))

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "bind entity to workflow with project where it's entity not exists" must {
    workflow ! BindEntity(1)
    projectProbe.expectMsgAnyClassOf(classOf[FindEntity])
    projectProbe.reply(EntityNotFound(1))
    "return BindEntityFailedBecauseItNotExists(1)" in {
      expectMsg(BindEntityFailedBecauseItNotExists(1))
    }
  }

  "bind entity to workflow with project where it's entity exists" must {
    workflow ! BindEntity(2)
    projectProbe.expectMsgAnyClassOf(classOf[FindEntity])
    projectProbe.reply(EntityFound(EntityMetaDefault(2, "entity2", "/path"), TestProbe().ref))
    "return BindEntitySuccessful(2)" in {
      expectMsg(BindEntitySuccessful(2))
    }
  }
}


