package ru.sber.cb.ap.gusli.actor.core.project.yamlfiles

import java.nio.file.Paths

import org.scalatest._
import ru.sber.cb.ap.gusli.actor.projects.DirectoryReadWriteConfig
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.YamlFileMapper

class YamlFileMapperSpec extends FlatSpec {
  
  "YamlCategoryMapper" should "read category file" in {
    val categoryDeserialized = YamlFileMapper.readCategoryFile(Paths.get(s"./src/test/resources/project_test-2/category/${DirectoryReadWriteConfig.categoryMetaFileName}"))
    
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
    assert(categoryMeta.get.name == "category")
    assert(categoryMeta.get.init.get("init.hql").contains("select 1"))
    assert(categoryMeta.get.init.get("init2.hql").contains("select 1"))
  }
  
  it should "read workflow file" in {
    val wfDeserialized = YamlFileMapper.readWorkflowFile(Paths.get(s"./src/test/resources/project_test-2/category/cb/ap/rb/wf-rb-sv/${DirectoryReadWriteConfig.workflowMetaFileName}"))
    assert(wfDeserialized.sql.contains(Set("rb-vek5555sel.sql", "rb-car433ds.sql")))
  }
  
  it should "transform file to WorkflowDtoMeta" in {
    val wfDtoMeta = YamlFileMapper.readToWorkflowDtoMeta(Paths.get("./src/test/resources/project_test-2/category/cb/ap/rb/wf-rb-sv/"))
    assert(wfDtoMeta.get.name.contains("rb-sv"))
    assert(wfDtoMeta.get.sql("rb-vek5555sel.sql").contains("select 2"))
    assert(wfDtoMeta.get.sql("rb-car433ds.sql").contains("select 2"))
  }
  
  it should "print None for empty fields" in {
    val wfFile = YamlFileMapper.readWorkflowFile(Paths.get("./src/test/resources/project_test-2/category/cb/ap/rb/wf-rb-sv/garbage/test-nones.yaml"))
    assert(wfFile.queue.isEmpty)
    assert(wfFile.init.isEmpty)
    assert(wfFile.param.isEmpty)
    assert(wfFile.stats.isEmpty)
  }
  it should "print Some() for {}, [] and \"\"" in {
    val wfFile = YamlFileMapper.readWorkflowFile(Paths.get("./src/test/resources/project_test-2/category/cb/ap/rb/wf-rb-sv/garbage/test-rewrite.yaml"))
    assert(wfFile.queue.contains(""))
    assert(wfFile.init.contains(List()))
    assert(wfFile.param.contains(Map()))
    assert(wfFile.stats.contains(Set()))
  }
  "YamlCategoryMapper.readToWorkflowOptionDto" should "read rewritable fields to Some()" in {
    val wfFile = YamlFileMapper.readToWorkflowOptionDto(Paths.get("./src/test/resources/project_test-2/category/cb/ap/rb/wf-rb-sv/garbage/"), "test-rewrite.yaml")
    assert(wfFile.get.queue.contains(""))
    assert(wfFile.get.init.contains(Map.empty))
    assert(wfFile.get.params.contains(Map.empty))
    assert(wfFile.get.stats.contains(Set.empty))
  }
  it should "read empty fields to None" in {
    val wfFile = YamlFileMapper.readToWorkflowOptionDto(Paths.get("./src/test/resources/project_test-2/category/cb/ap/rb/wf-rb-sv/garbage/"), "test-nones.yaml")
    assert(wfFile.get.queue.isEmpty)
    assert(wfFile.get.init.isEmpty)
    assert(wfFile.get.params.isEmpty)
    assert(wfFile.get.stats.isEmpty)
  }
}

