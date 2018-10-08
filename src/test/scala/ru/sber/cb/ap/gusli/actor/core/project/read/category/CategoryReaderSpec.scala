package ru.sber.cb.ap.gusli.actor.core.project.read.category

import java.nio.file.Paths

import akka.actor.ActorRef
import ru.sber.cb.ap.gusli.actor.core.Category.{GetSubcategories, GetWorkflows, SubcategorySet, WorkflowSet}
import ru.sber.cb.ap.gusli.actor.core._
import ru.sber.cb.ap.gusli.actor.projects.read.category.CategoryFolderReader.ReadCategoryFolder
import ru.sber.cb.ap.gusli.actor.projects.read.category.{CategoryFolderReader, CategoryFolderReaderMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.YamlFileMapperRead

class CategoryReaderSpec extends ActorBaseTest("DirectoryProjectSpec") {
  val correctPath = Paths.get("./src/test/resources/project_test-2/category")
  val project: ActorRef = system.actorOf(Project(ProjectMetaDefault("test_project2")))
  val categoryMeta = YamlFileMapperRead.readToCategoryMeta(correctPath).get
  val category: ActorRef = system.actorOf(Category(categoryMeta, project))
  val categoryFolderReader: ActorRef = system.actorOf(CategoryFolderReader(CategoryFolderReaderMetaDefault(correctPath, category)))
  type Category = ActorRef
  "CategoryFolderReader" when {
    var cbCategory: Category = null
    var apCategory: Category = null
    var rbCategory: Category = null
    
    "receive ReadCategoryFolder" should {
      "read categories" in {
        categoryFolderReader ! ReadCategoryFolder()
        Thread.sleep(1000)
      }
    }
    "and category receiving ListSubcategories" should {
      "send back list with size 1" in {
        category ! GetSubcategories()
        expectMsgPF() {
          case SubcategorySet(actorList) =>
            assert(actorList.size == 1)
            cbCategory = actorList.head
        }
      }
    }
    "cb category receiving ListWorkflow" should {
      "send back list with size 5" in {
        cbCategory ! GetWorkflows()
        expectMsgPF() {
          case WorkflowSet(actorList) => assert(actorList.size == 5)
        }
      }
    }
    "receiving ListSubcategory send back SubcategoryList with size 1 (ap)" in {
      cbCategory ! GetSubcategories()
      expectMsgPF() {
        case SubcategorySet(list) =>
          assert(list.size == 1)
          apCategory = list.head
      }
    }
    "ap category should include rb category" in {
      apCategory ! GetSubcategories()
      expectMsgPF() {
        case SubcategorySet(list) =>
          assert(list.size == 1)
          rbCategory = list.head
      }
    }
    "rb category receiving ListWorkflow send back WorkflowList with size 3" in {
      rbCategory ! GetWorkflows()
      expectMsgPF() {
        case WorkflowSet(list) => assert(list.size == 3)
      }
    }
  }
}
