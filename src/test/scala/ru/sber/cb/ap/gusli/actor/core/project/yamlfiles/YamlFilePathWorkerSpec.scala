package ru.sber.cb.ap.gusli.actor.core.project.yamlfiles

import java.nio.file.Paths

import ru.sber.cb.ap.gusli.actor.core.ActorBaseTest
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.YamlFilePathWorker._

class YamlFilePathWorkerSpec extends ActorBaseTest("YamlFilePathWorkerSpec") {
  val path = "./src/test/resources/project_test-2/entity/105000000 entity-root/105060000 entity-parent"
  
  "A YamlFilePathWorker" when {
  
    "receive getParentIdFromPath(./entity/1 a/2 b/)" should {
      "return 1" in {
        val parentId = extractParentIdFromPath(Paths.get("./entity/1 a/2 b/"))
        assert(parentId.contains(1))
      }
    }
    "receive getParentIdFromPath(./entity/1 a)" should {
      "return 0" in {
        val parentId = extractParentIdFromPath(Paths.get("./entity/1 a"))
        assert(parentId.contains(0))
      }
    }
    "receive getParentIdFromPath(./entity/)" should {
      "return None" in {
        val parentId = extractParentIdFromPath(Paths.get("./entity"))
        assert(parentId.isEmpty)
      }
    }
    "receive getParentIdFromPath(./entity/1 a/2 b/entity.yaml)" should {
      "return 1" in {
        val parentId = extractParentIdFromPath(Paths.get("./1 a/2 b/entity.yaml"))
        assert(parentId.contains(1))
      }
    }
    "receive isYaml(./entity/entity.yaml)" should {
      "return true" in {
        assert(isYaml(Paths.get("./src/test/resources/project_test-2/entity/entity.yaml")))
      }
    }
    "receive isYaml(./entity/entity)" should {
      "return false" in {
        assert(isEntityYaml(Paths.get("./entity.yaml")))
      }
    }
    "receive isEntityDotYaml(./entity.yaml)" should {
      "return true" in {
        assert(!isEntityYaml(Paths.get("./not-entity.yaml")))
      }
    }
    "receive isEntityDotYaml(./entity/entity)" should {
      "return false" in {
        assert(!isYaml(Paths.get("./entity/")))
      }
    }
    "receive parseIdAndNameFromYaml(23 lalala.yaml)" should {
      "return (23, lalala)" in {
        assert(parseIdAndNameFromYaml(Paths.get("23 lalala.yaml")) == (23, "lalala"))
      }
    }
    "receive parseIdAndNameFrom(23 lalala)" should {
      "return (23, lalala)" in {
        assert(parseIdAndNameFrom(Paths.get("23 lalala")) == (23, "lalala"))
      }
    }
    "receive getAllValidEntityChilds(/src/test/resources/project_test-2/entity)" should {
      "return 1 folder" in {
        val childs = getAllValidEntityChilds(Paths.get("./src/test/resources/project_test-2/entity"))
        assert(childs(0).getFileName.toString == "105000000 entity-root")
        assert(childs.size == 1)
      }
    }
    "receive getAllValidEntityChilds(./src/test/resources/project_test-2/entity/105000000 entity-root/105060000 entity-parent/105067000 entity-children)" should {
      "return list with size 3" in {
        val childs = getAllValidEntityChilds(Paths.get("./src/test/resources/project_test-2/entity/105000000 entity-root/105060000 entity-parent/105067000 entity-children"))
        assert(childs.size == 3)
      }
    }
    "receive getAllValidCategoryChilds(./src/test/resources/project_test-2/category/cb)" should {
      "return list with size 3" in {
        val childs = getAllValidCategoryChilds(Paths.get("./src/test/resources/project_test-2/category/cb"), scala.collection.mutable.ArrayBuffer("cb-wf1.sql", "AP"))
        assert(childs.size == 4)
      }
    }
  }
}