package ru.sber.cb.ap.gusli.actor.core.project

import akka.actor.ActorRef
import akka.testkit.{TestKit, TestProbe}
import ru.sber.cb.ap.gusli.actor.core.Entity.{AddChildEntity, ChildrenEntityList, EntityCreated, GetChildren}
import ru.sber.cb.ap.gusli.actor.core.Project._
import ru.sber.cb.ap.gusli.actor.core._
import ru.sber.cb.ap.gusli.actor.core.search.EntitySearcher

class EntityFoundSpec extends ActorBaseTest("EntityFoundSpec") {

  val project: ActorRef = system.actorOf(Project(ProjectMetaDefault("project")), "project")

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Project" when {
    "add entity to entity root" should {
      var entityRoot: ActorRef = null
      var entity1Ref: ActorRef = null
      val entity1Meta = EntityMetaDefault(1, "entity1", "/path1", None)
      "receive GetEntityRoot" in {
        project ! GetEntityRoot()
        expectMsgPF() {
          case EntityRoot(root) =>
            entityRoot = root
            root ! GetChildren()
            expectMsg(ChildrenEntityList(Nil))

            root ! AddChildEntity(entity1Meta)
            expectMsgPF() {
              case EntityCreated(entity1) =>
                entity1Ref = entity1
            }
        }
      }

      "test GetChildren" in {
        entityRoot ! GetChildren()
        expectMsg(ChildrenEntityList(Seq(entity1Ref)))
      }

      "test EntitySearcher" in {
        val probe: TestProbe = TestProbe()
        system.actorOf(EntitySearcher(Seq(entityRoot), 1, probe.ref))
        probe.expectMsg(EntityFound(entity1Meta, entity1Ref))
      }

      "send back EntityFound" in {
        project ! FindEntity(1)
        expectMsg(EntityFound(entity1Meta, entity1Ref))
      }
    }
  }
}

