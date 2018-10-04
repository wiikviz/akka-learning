package ru.sber.cb.ap.gusli.actor.projects.yamlfiles

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, Paths}

import com.fasterxml.jackson.annotation.JsonInclude
import ru.sber.cb.ap.gusli.actor.core.{CategoryMetaDefault, WorkflowMetaDefault}
import ru.sber.cb.ap.gusli.actor.projects.{DirectoryReadWriteConfig, MetaFieldsComparer}

object YamlFileMapperWrite {

  //TODO В АНАЛОГИЧНОМ ФАЙЛЕ С НАЗВАНИЕМ READ НАХОДЯТСЯ ПРИМЕРЫ
  //СОЗДАЕМ МЕТЫ ДЛЯ ФАЙЛОВ ПО WF И КАТЕГОРИЯМ, ИСПОЛЬЗУЯ ДИФФЕРЫ
  //ПОКА ПРЕДВАРИТЕЛЬНО БУДЕТ 2 МЕТОДА: writeObj(Wf)ToFile(path, child, parent)
  
  def writeCategoryMeta(path: Path, parent: CategoryMetaDefault, child: CategoryMetaDefault)=
    writeIfDifferent(path: Path, parent: CategoryMetaDefault, child: CategoryMetaDefault)
  
  def writeIfDifferent(path: Path, parent: CategoryMetaDefault, child: CategoryMetaDefault) =
    if (isDifferent(parent, child)) {
      val inheritedMeta = inheritMeta(parent, child)
      val metaFile = path.resolve(DirectoryReadWriteConfig.categoryMetaFileName)
      
      writeMeta(metaFile, inheritedMeta)
      writeContent(path, parent, child)
    }
  
  private def inheritMeta(p: CategoryMetaDefault, c: CategoryMetaDefault): CategoryFileFields = {
    CategoryFileFields(
      grenki   = MetaFieldsComparer.diffField(p.grenkiVersion, c.grenkiVersion),
      queue    = MetaFieldsComparer.diffField(p.queue, c.queue),
      user     = MetaFieldsComparer.diffField(p.user, c.user),
      init     = Some(MetaFieldsComparer.diffSet(p.init.keySet, c.init.keySet).toList),
      map      = Some(MetaFieldsComparer.diffSet(p.sqlMap.keySet, c.sqlMap.keySet).toList),
      param    = Some(MetaFieldsComparer.diffMap(p.params, c.params)),
      stats    = Some(MetaFieldsComparer.diffSet(p.stats, c.stats).map(_.toInt)),
      entities = Some(MetaFieldsComparer.diffSet(p.entities, c.entities).map(_.toInt))
    )
  }
  
  private def writeMeta[T](path: Path, value: T): Unit = value match {
    case v: CategoryFileFields => writeFieldsToFile(path, v)
  }
  
  private def writeContent[T](path: Path, p: T, c: T) = {
    val metas = (p, c)
    (p, c) match {
      case (p: WorkflowMetaDefault, c: WorkflowMetaDefault) =>
        writeNewContent(path, p.sqlMap, c.sqlMap)
        writeNewContent(path, p.init, c.init)
        writeNewContent(path, p.sql, c.sql)
      case (p: CategoryMetaDefault, c: CategoryMetaDefault) =>
        writeNewContent(path, p.sqlMap, c.sqlMap)
        writeNewContent(path, p.init, c.init)
    }
  }
  
  def writeNewContent(path: Path, p: Map[String, String], c: Map[String, String]): Unit = {
    val newContent = (c.toSet diff p.toSet).toMap
    for ((fileName, fileContent) <- newContent) {
      writeTextFileToDirectory(fileName, fileContent, path)
    }
    
  }
  
  private def writeFieldsToFile[T](path: Path, fields: T): Unit = {
    val mapper = initMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT)
    val f = new File(path.toString)
    mapper.writeValue(f, fields)
  }
  
  private def writeTextFileToDirectory(fileName: String, fileContent: String, dir: Path): Path =
    Files.write(dir.resolve(fileName), fileContent.getBytes(StandardCharsets.UTF_8))
  
  private def isDifferent(child: CategoryMetaDefault, parent: CategoryMetaDefault) =
    child.copy(name = parent.name) != parent
}
