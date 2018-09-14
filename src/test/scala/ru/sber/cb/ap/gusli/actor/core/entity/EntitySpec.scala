package ru.sber.cb.ap.gusli.actor.core.entity

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Entity._
import ru.sber.cb.ap.gusli.actor.core.{Entity, EntityMetaDefault}

class EntitySpec extends TestKit(ActorSystem("EntitySpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }
  
  "An Entity" when {
    val entity: ActorRef = system.actorOf(Entity(EntityMetaDefault(0, "root-entity", "file.ent")))
    "receive GetEntityMeta" should {
      "send back EntityMetaResponse" in {
        entity ! GetEntityMeta()
        expectMsg(EntityMetaResponse(0, "root-entity", "file.ent"))
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
        entity ! AddChildEntity(EntityMetaDefault(1, "child-entity", "file.ent"))
        expectMsgPF() {
          case EntityCreated(childEntity) =>
            childEntity ! GetEntityMeta()
            expectMsg(EntityMetaResponse(1, "child-entity", "file.ent"))
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