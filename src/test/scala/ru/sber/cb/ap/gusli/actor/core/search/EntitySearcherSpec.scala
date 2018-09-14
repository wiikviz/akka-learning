package ru.sber.cb.ap.gusli.actor.core.search

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Entity.{ChildrenEntityList, EntityMetaResponse, GetChildren, GetEntityMeta}
import ru.sber.cb.ap.gusli.actor.core.EntityMetaDefault
import ru.sber.cb.ap.gusli.actor.core.Project.{EntityFound, EntityNotFound}


class EntitySearcherSpec extends TestKit(ActorSystem("EntitySearcherSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "EntitySearcher" must {
    "send back EntityFound" in {
      val entity1 = TestProbe()
      val entity2 = TestProbe()
      val meta1 = EntityMetaDefault(1, "entity1", "/entity1")
      val meta2 = EntityMetaDefault(2, "entity2", "/entity2")
      val entityRequester = TestProbe()

      system.actorOf(EntitySearcher(entityRefs = Seq(entity1.ref, entity2.ref), 2, entityRequester.ref))
      entity1.expectMsg(GetEntityMeta())
      entity1.reply(EntityMetaResponse(meta1.id, meta1.name, meta1.path))
      entity2.expectMsg(GetEntityMeta())
      entity2.reply(EntityMetaResponse(meta2.id, meta2.name, meta2.path))

      entityRequester.expectMsg(EntityFound(EntityMetaResponse(meta2.id, meta2.name, meta2.path), entity2.ref))
    }

    "send back EntityNotFound" in {
      val entity1 = TestProbe()
      val entity2 = TestProbe()
      val meta1 = EntityMetaDefault(1, "entity1", "/entity1")
      val meta2 = EntityMetaDefault(2, "entity2", "/entity2")
      val entityRequester = TestProbe()

      system.actorOf(EntitySearcher(entityRefs = Seq(entity1.ref, entity2.ref), 3, entityRequester.ref))
      entity1.expectMsg(GetEntityMeta())
      entity1.reply(EntityMetaResponse(meta1.id, meta1.name, meta1.path))
      entity1.expectMsg(GetChildren())
      entity1.reply(ChildrenEntityList(Nil))

      entity2.expectMsg(GetEntityMeta())
      entity2.reply(EntityMetaResponse(meta2.id, meta2.name, meta2.path))
      entity2.expectMsg(GetChildren())
      entity2.reply(ChildrenEntityList(Nil))

      entityRequester.expectMsg(EntityNotFound(3))
    }
  }
}

