package ru.sber.cb.ap.gusli.actor.core.project

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import ru.sber.cb.ap.gusli.actor.core.Entity.{AddChildEntity, ChildrenEntityList, EntityCreated, GetChildren}
import ru.sber.cb.ap.gusli.actor.core.Project._
import ru.sber.cb.ap.gusli.actor.core._


class FindEntitySpec extends TestKit(ActorSystem("FindEntitySpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  val project: ActorRef = system.actorOf(Project(ProjectMetaDefault("project")), "project")

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Project" when {
    "it's have no entities" should {
      "when receive FindEntity(1) send back EntityNotFound(1)" in {
        project ! FindEntity(1)
        expectMsg(EntityNotFound(1))
      }
    }

    "add entity to entity root" should {
      var entity1Ref: Option[ActorRef] = None
      val entity1Meta = EntityMetaDefault(1, "entity1", "/path1")
      "receive GetEntityRoot" in {
        project ! GetEntityRoot()
        expectMsgPF() {
          case EntityRoot(root) =>
            root ! GetChildren()
            expectMsg(ChildrenEntityList(Nil))

            root ! AddChildEntity(entity1Meta)
            expectMsgPF() {
              case EntityCreated(entity1) =>
                entity1Ref = Some(entity1)
            }
        }
      }

      "send back EntityFound" in {
        project ! FindEntity(1)
        expectMsg(EntityFound(entity1Meta, entity1Ref.get))
      }
    }
  }
}

