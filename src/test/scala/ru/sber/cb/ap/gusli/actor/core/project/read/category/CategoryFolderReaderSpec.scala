package ru.sber.cb.ap.gusli.actor.core.project.read.category

import java.nio.file.Paths

import akka.actor.ActorRef
import ru.sber.cb.ap.gusli.actor.core.Category.{ListSubcategory, ListWorkflow, SubcategoryList, WorkflowList}
import ru.sber.cb.ap.gusli.actor.core._
import ru.sber.cb.ap.gusli.actor.projects.read.category.CategoryFolderReader.ReadCategoryFolder
import ru.sber.cb.ap.gusli.actor.projects.read.category.{CategoryFolderReader, CategoryFolderReaderMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.YamlFileMapper

class CategoryFolderReaderSpec extends ActorBaseTest("DirectoryProjectSpec") {
  val correctPath = Paths.get("./src/test/resources/project_test-2/category")
  val project: ActorRef = system.actorOf(Project(ProjectMetaDefault("test_project2")))
  val categoryMeta = YamlFileMapper.readToCategoryMeta(correctPath).get
  val category: ActorRef = system.actorOf(Category(categoryMeta, project))
  val categoryFolderReader: ActorRef = system.actorOf(CategoryFolderReader(CategoryFolderReaderMetaDefault(correctPath, category)))
  type Category = ActorRef
  "CategoryFolderReader" when {
    var cbCategory: Category = null
    "receive ReadCategoryFolder" should {
      "read categories" in {
        categoryFolderReader ! ReadCategoryFolder()
        Thread.sleep(1000)
      }
    }
    "and category receiving ListSubcategories" should {
      "send back list with size 1" in {
        category ! ListSubcategory()
        expectMsgPF() {
          case SubcategoryList(actorList) =>
            assert(actorList.size == 1)
            cbCategory = actorList.head
        }
      }
    }
    "cb category receiving ListSubcategories" should {
      "send back list with size 1" in {
        cbCategory ! ListSubcategory()
        expectMsgPF() {
          case SubcategoryList(actorList) => assert(actorList.size == 1)
        }
      }
    }
    "cb category receiving ListWorkflow" should {
      "send back list with size 5" in {
        cbCategory ! ListWorkflow()
        expectMsgPF() {
          case WorkflowList(actorList) => assert(actorList.size == 5)
        }
      }
    }
  }
}
