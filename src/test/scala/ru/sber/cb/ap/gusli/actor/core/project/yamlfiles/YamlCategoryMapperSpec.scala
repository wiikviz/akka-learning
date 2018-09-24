package ru.sber.cb.ap.gusli.actor.core.project.yamlfiles

import org.scalatest._
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.YamlCategoryMapper

class YamlCategoryMapperSpec extends FlatSpec {
  
  "YamlCategoryMapper" should "return case class without param" in {
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
    assert(categoryDeserialized.init.get(0) == "init.hql")
    assert(categoryDeserialized.init.get(1) == "init2.hql")
    assert(categoryDeserialized.map.get(0) == "map.config")
    assert(categoryDeserialized.map.get(1) == "map2.config")
    assert(categoryDeserialized.stats.get.head == 2)
    assert(categoryDeserialized.entities.get.head == 105067300)
  }
  
    it should "return case class with param" in {
    val catYamlContext =
      "param:" +
        "\n  p1: 1" +
        "\n  p2: b"
    val categoryDeserialized = YamlCategoryMapper.read(catYamlContext)
  
    assert(categoryDeserialized.param.get("p1") == 1)
    assert(categoryDeserialized.param.get("p2") == "b")
  }
}
