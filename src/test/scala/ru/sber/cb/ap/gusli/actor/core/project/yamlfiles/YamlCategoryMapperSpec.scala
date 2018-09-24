package ru.sber.cb.ap.gusli.actor.core.project.yamlfiles

import org.scalatest._
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.{CategoryFile, YamlCategoryMapper}

class YamlCategoryMapperSpec extends FlatSpec {
  
  "YamlCategoryMapper without param" should "return case class without param" in {
    val catYamlContext =
      "grenki: 0.2" +
         "\nqueue: root.platform" +
         "\nuser: pupkin" +
         "\ninit:" +
         "\n  - init.hql" +
         "\n  - init2.hql" +
         "\nmap:" +
         "\n  - map.config" +
         "\n  - map2.config" +
         "\nstats:" +
         "\n  - 2" +
         "\nentities: 105067300"
    val categoryDeserialized = YamlCategoryMapper.read(catYamlContext)
    
    assert(categoryDeserialized.grenki.contains("0.2"))
    assert(categoryDeserialized.queue.contains("root.platform"))
    assert(categoryDeserialized.user.contains("pupkin"))
    assert(categoryDeserialized.init(0) == "init.hql")
    assert(categoryDeserialized.init(1) == "init2.hql")
    assert(categoryDeserialized.map(0) == "map.config")
    assert(categoryDeserialized.map(1) == "map2.config")
    assert(categoryDeserialized.stats.head == 2)
    assert(categoryDeserialized.entities.head == 105067300)
  }
  
  "YamlCategoryMapper with param" should "return case class with param" in {
    val catYamlContext =
      "param:" +
        "\n  p1: 1" +
        "\n  p2: b"
    val categoryDeserialized = YamlCategoryMapper.read(catYamlContext)
  
    assert(categoryDeserialized.param.get("p1") == Some(1))
    assert(categoryDeserialized.param.get("p2") == Some("b"))
  }
}
