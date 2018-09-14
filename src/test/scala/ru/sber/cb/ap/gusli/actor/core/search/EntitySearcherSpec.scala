package ru.sber.cb.ap.gusli.actor.core.search

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.EntityMetaDefault
import ru.sber.cb.ap.gusli.actor.core.Project.EntityFound


class EntitySearcherSpec extends TestKit(ActorSystem("EntitySearcherSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {
  val entity1 = TestProbe()
  val entity2 = TestProbe()
  val metaEntity1 = EntityMetaDefault(1, "entity1", "/entity1")
  val metaEntity2 = EntityMetaDefault(2, "entity2", "/entity2")
  val entityRequester = TestProbe()

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A EntitySearcher" must {
    "send back EntityFound" in {
      system.actorOf(EntitySearcher(entityRefs = Seq(entity1.ref, entity2.ref), 2, entityRequester.ref))
      entityRequester.expectMsg(EntityFound(metaEntity2, entity2.ref))
    }
  }
}

