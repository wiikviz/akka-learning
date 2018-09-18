package ru.sber.cb.ap.gusli.actor.core.project.yamlfiles

import java.nio.file.Paths

import ru.sber.cb.ap.gusli.actor.core.ActorBaseTest
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.YamlFilePathWorker._

class YamlFilePathWorkerSpec extends ActorBaseTest("YamlFilePathWorkerSpec") {
  val path = ".\\src\\test\\resources\\project_test-2\\entity\\105000000 entity-root\\105060000 entity-parent"
  
  "A YamlFilePathWorker" when {
  
    "receive getParentIdFromPath(./entity/1 a/2 b/)" should {
      "return 1" in {
        val parentId = getParentIdFromPath(Paths.get("./entity/1 a/2 b/"))
        assert(parentId.contains(1))
      }
    }
    "receive getParentIdFromPath(./entity/1 a)" should {
      "return 0" in {
        val parentId = getParentIdFromPath(Paths.get("./entity/1 a"))
        assert(parentId.contains(0))
      }
    }
    "receive getParentIdFromPath(./entity/1 a/2 b/)" should {
      "return None" in {
        val parentId = getParentIdFromPath(Paths.get("./entity"))
        assert(parentId.isEmpty)
      }
    }
    "receive isYaml(./entity/entity.yaml)" should {
      "return true" in {
        assert(isYaml(Paths.get(".\\src\\test\\resources\\project_test-2\\entity\\entity.yaml")))
      }
    }
    "receive isYaml(./entity/entity)" should {
      "return false" in {
        assert(!isYaml(Paths.get("./entity/")))
      }
    }
    "receive parseIdAndNameFromYaml(23 lalala.yaml)" should {
      "return (23, lalala)" in {
        assert(parseIdAndNameFromYaml("23 lalala.yaml") == (23, "lalala"))
      }
    }
    "receive parseIdAndNameFrom(23 lalala)" should {
      "return (23, lalala)" in {
        assert(parseIdAndNameFrom("23 lalala") == (23, "lalala"))
      }
    }
  }
}