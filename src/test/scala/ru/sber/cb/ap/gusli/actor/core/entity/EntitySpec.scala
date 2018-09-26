package ru.sber.cb.ap.gusli.actor.core.entity

import akka.actor.ActorRef
import ru.sber.cb.ap.gusli.actor.core.Entity._
import ru.sber.cb.ap.gusli.actor.core.{ActorBaseTest, Entity, EntityMetaDefault}

class EntitySpec extends ActorBaseTest("EntitySpec") {

  "An Entity" when {
    val meta = EntityMetaDefault(0, "root-entity", "file.ent", None)
    val entity: ActorRef = system.actorOf(Entity(meta))
    "GetEntityMeta is received" should {
      "EntityMetaResponse should be sent back" in {
        entity ! GetEntityMeta()
        expectMsg(EntityMetaResponse(meta))
      }
    }

    "GetChildren is received" should {
      "an empty ChildrenEntityList should be sent back" in {
        entity ! GetChildren()
        expectMsg(ChildrenEntityList(Nil))
      }
    }

    "AddChildEntity  is received" should {
      "EntityCreated should be sent back" in {
        val meta = EntityMetaDefault(1, "child-entity", "file.ent", None)
        entity ! AddChildEntity(meta)
        expectMsgPF() {
          case EntityCreated(childEntity) =>
            childEntity ! GetEntityMeta()
            expectMsg(EntityMetaResponse(meta))
        }
      }
    }

    "GetChildren is received again" should {
      "a list of size 1 should be sent back" in {
        entity ! GetChildren()
        expectMsgPF() {
          case ChildrenEntityList(list) => assert(list.size == 1)
        }
      }
    }
  }
}