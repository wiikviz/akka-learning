package ru.sber.cb.ap.gusli.actor.core.entity

import akka.actor.ActorRef
import akka.testkit.TestKit
import ru.sber.cb.ap.gusli.actor.core.Entity._
import ru.sber.cb.ap.gusli.actor.core.{ActorBaseTest, Entity, EntityMetaDefault}

class EntitySpec extends ActorBaseTest("EntitySpec") {
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "An Entity" when {
    val meta = EntityMetaDefault(0, "root-entity", "file.ent", None)
    val entity: ActorRef = system.actorOf(Entity(meta))
    "receive GetEntityMeta" should {
      "send back EntityMetaResponse" in {
        entity ! GetEntityMeta()
        expectMsg(EntityMetaResponse(meta))
      }
    }

    "receive GetChildren" should {
      "send back an empty ChildrenEntityList" in {
        entity ! GetChildren()
        expectMsg(ChildrenEntityList(Nil))
      }
    }

    "receive AddChildEntity" should {
      "send back EntityCreated" in {
        val meta = EntityMetaDefault(1, "child-entity", "file.ent", None)
        entity ! AddChildEntity(meta)
        expectMsgPF() {
          case EntityCreated(childEntity) =>
            childEntity ! GetEntityMeta()
            expectMsg(EntityMetaResponse(meta))
        }
      }
    }

    "receive again GetChildren" should {
      "send back empty ChildrenEntityList" in {
        entity ! GetChildren()
        expectMsgPF() {
          case ChildrenEntityList(list) => assert(list.size == 1)
        }
      }
    }
  }
}