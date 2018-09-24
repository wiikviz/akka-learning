package ru.sber.cb.ap.gusli.actor.core.project.yamlfiles

import java.nio.file.Paths

import org.scalatest._
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.YamlFileMapper

class YamlFileMapperSpec extends FlatSpec {
  
  "YamlCategoryMapper" should "read category file" in {
    val categoryDeserialized = YamlFileMapper.readCategoryFile(Paths.get("./src/test/resources/project_test-2/category/meta.yaml"))
    
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
  
  it should "transform data from file to CategoryMeta" in {
    val categoryMeta = YamlFileMapper.readToCategoryMeta(Paths.get("./src/test/resources/project_test-2/category/"))
    assert(categoryMeta.name == "category")
    assert(categoryMeta.init.get("init.hql").contains("select 1"))
    assert(categoryMeta.init.get("init2.hql").contains("select 1"))
  }
  
  it should "read workflow file" in {
    val wfDeserialized = YamlFileMapper.readWorkflowFile(Paths.get("./src/test/resources/project_test-2/category/cb/ap/rb/wf-rb-sv/meta.yaml"))
    assert(wfDeserialized.sql.contains(Set("rb-vek5555sel.sql", "rb-car433ds.sql")))
  }
  
  it should "transform file to WorkflowDtoMeta" in {
    val wfDtoMeta = YamlFileMapper.readToWorkflowDtoMetaFromFolder(Paths.get("./src/test/resources/project_test-2/category/cb/ap/rb/wf-rb-sv/"))
    assert(wfDtoMeta.name.contains("rb-sv"))
    assert(wfDtoMeta.sql("rb-vek5555sel.sql").contains("select 2"))
    assert(wfDtoMeta.sql("rb-car433ds.sql").contains("select 2"))
  }
}
