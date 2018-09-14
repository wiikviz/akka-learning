package ru.sber.cb.ap.gusli.actor.core

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Entity._

class EntitySpec extends TestKit(ActorSystem("EntitySpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }
  
  "An Entity" when {
    val entity: ActorRef = system.actorOf(Entity(EntityMetaDefault(0, "root-entity", "file.ent")))
    "receive GetEntityMeta" should {
      entity ! GetEntityMeta()
      "send back EntityMetaResponse" in {
        expectMsg(EntityMetaResponse(0, "root-entity", "file.ent"))
      }
    }
  }
}