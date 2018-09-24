package ru.sber.cb.ap.gusli.actor.core.project.yamlfiles

import java.nio.file.Paths

import org.scalatest._
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.YamlCategoryMapper

class YamlCategoryMapperSpec extends FlatSpec {
  
  "YamlCategoryMapper" should "return case class from file" in {
    val categoryDeserialized = YamlCategoryMapper.read(Paths.get("./src/test/resources/project_test-2/category/category.yaml"))
    
    assert(categoryDeserialized.grenki.contains("0.2"))
    assert(categoryDeserialized.queue.contains("root.platform"))
    assert(categoryDeserialized.user.contains("pupkin"))
    assert(categoryDeserialized.init.get(0) == "init.hql")
    assert(categoryDeserialized.init.get(1) == "init2.hql")
    assert(categoryDeserialized.map.get(0) == "map.config")
    assert(categoryDeserialized.map.get(1) == "map2.config")
    assert(categoryDeserialized.param.get("p1") == "1")
    assert(categoryDeserialized.param.get("p2") == "I'm String")
    assert(categoryDeserialized.stats.get.head == 2)
    assert(categoryDeserialized.entities.get.head == 105067300)
  }
  
  it should "return CategoryMeta" in {
    val categoryMeta = YamlCategoryMapper.readToCategoryMeta(Paths.get("./src/test/resources/project_test-2/category/"))
    assert(categoryMeta.name == "category")
    assert(categoryMeta.init.get("init.hql").contains("select 1"))
    assert(categoryMeta.init.get("init2.hql").contains("select 1"))
  }
}
