package ru.sber.cb.ap.gusli.actor.core.serialize

import java.nio.file.Paths

import akka.actor.ActorRef
import akka.testkit.TestKit
import org.scalatest.Ignore
import ru.sber.cb.ap.gusli.actor.core.Category.{apply => _, _}
import ru.sber.cb.ap.gusli.actor.core.Entity.{EntityMetaResponse, GetEntityMeta}
import ru.sber.cb.ap.gusli.actor.core.Project.{apply => _, _}
import ru.sber.cb.ap.gusli.actor.core.{ActorBaseTest, CategoryMetaDefault, EntityMetaDefault, ProjectMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.read.DirectoryProjectReader
import ru.sber.cb.ap.gusli.actor.projects.read.DirectoryProjectReader._

class DirectoryProjectReaderSpec extends ActorBaseTest("DirectoryProjectSpec") {
  val correctPath = Paths.get("./src/test/resources/project_test-2")
  val incorrectPath = Paths.get("incorrect_path_here")
  val directoryProjectReader: ActorRef = system.actorOf(DirectoryProjectReader(correctPath))

  "Directory project reader" when {
    "receive ReadProject(correctPath)" should {
      var project: ActorRef = null
      var rootCategory: ActorRef = null
      var cbCategory: ActorRef = null
      var apCategory: ActorRef = null
      var rbCategory: ActorRef = null
      "send back ProjectReaded(project)" in {
        directoryProjectReader ! ReadProject()
        Thread.sleep(1000)
        expectMsgPF() {
          case ProjectReaded(inputProject) => project = inputProject
        }
      }
      "and project receiving GetProjectMeta should send back ProjectMetaResponse" in {
        project ! GetProjectMeta()
        expectMsg(ProjectMetaResponse(ProjectMetaDefault("project_test-2")))
      }
      "receiving GetEntityRoot should send back EntityRoot" in {
        project ! GetEntityRoot()
        expectMsgPF() {
          case EntityRoot(root) =>
            root ! GetEntityMeta()
            expectMsg(EntityMetaResponse(EntityMetaDefault(0, "entity", "", None)))
        }
      }
      "receiving GetCategoryRoot should send back CategoryRoot" in {
        project ! GetCategoryRoot()
        expectMsgPF() {
          case CategoryRoot(root) =>
            rootCategory = root
            root ! GetCategoryMeta()
            expectMsgPF() {
              case CategoryMetaResponse(meta) =>
                assert(meta.name == "category")
                assert(meta.grenkiVersion.contains("0.2"))
                assert(meta.init("init.hql").contains("select 1"))
            }
        }
      }
      "receiving FindEntity(0) should send back EntityFound" in {
        project ! FindEntity(0)
        expectMsgAnyClassOf(classOf[EntityFound])
      }
      "receiving FindEntity(105000000) should send back EntityFound" in {
        project ! FindEntity(105000000)
        expectMsgAnyClassOf(classOf[EntityFound])
      }
      "receiving FindEntity(105060000) should send back EntityFound" in {
        project ! FindEntity(105060000)
        expectMsgAnyClassOf(classOf[EntityFound])
      }
      "receiving FindEntity(105067000) should send back EntityFound" in {
        project ! FindEntity(105067000)
        expectMsgAnyClassOf(classOf[EntityFound])
      }
      "receiving FindEntity(105067100) should send back EntityFound" in {
        project ! FindEntity(105067100)
        expectMsgAnyClassOf(classOf[EntityFound])
      }
      "receiving FindEntity(105067200) should send back EntityFound" in {
        project ! FindEntity(105067200)
        expectMsgAnyClassOf(classOf[EntityFound])
      }
      "receiving FindEntity(105067300) should send back EntityFound" in {
        project ! FindEntity(105067300)
        expectMsgAnyClassOf(classOf[EntityFound])
      }
      "receiving FindEntity(1) should send back EntityNotFound" in {
        project ! FindEntity(1)
        expectMsg(EntityNotFound(1))
      }
      "and root-category receiving ListSubcategory should send back SubcategoryList with 'cb'" in {
        rootCategory ! ListSubcategory()
        expectMsgPF() {
          case SubcategoryList(list) =>
            assert(list.size == 1)
            cbCategory = list(0)
        }
      }
      "and category receiving ListWorkflow should send back WorkflowList with size 5" in {
        cbCategory ! ListWorkflow()
        expectMsgPF() {
          case WorkflowList(list) => assert(list.size == 5)
        }
      }
      "receiving ListSubcategory send back SubcategoryList with size 1 (ap)" in {
        cbCategory ! ListSubcategory()
        expectMsgPF() {
          case SubcategoryList(list) =>
            assert(list.size == 1)
            apCategory = list(0)
        }
      }
      "apCategory should include rbCategory" in {
        apCategory ! ListSubcategory()
        expectMsgPF() {
          case SubcategoryList(list) =>
            assert(list.size == 1)
            rbCategory = list(0)
        }
      }
      "rbCategory receiving ListWorkflow send back WorkflowList with size 3" in {
        rbCategory ! ListWorkflow()
        expectMsgPF() {
          case WorkflowList(list) => assert(list.size == 3)
        }
      }
    }
  }

}
