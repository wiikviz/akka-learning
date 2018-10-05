package ru.sber.cb.ap.gusli.actor.projects.yamlfiles

import java.nio.file.{Files, Path}

import ru.sber.cb.ap.gusli.actor.core.CategoryMetaDefault
import ru.sber.cb.ap.gusli.actor.core.dto.WorkflowDto
import ru.sber.cb.ap.gusli.actor.projects.{DirectoryReadWriteConfig, MetaFieldsComparer}

object YamlFileMapperWrite {
  type CatMeta = CategoryMetaDefault
  type WfMeta = WorkflowDto

  def writeMeta(path: Path, parent: CatMeta, child: CatMeta): Unit =
    writeIfDifferent(path: Path, parent, child)

  def writeMeta(path: Path, parent: CatMeta, child: WfMeta): Unit =
    writeIfDifferent(path: Path, parent, child)
  
  def writeIfDifferent(path: Path, p: CatMeta, c: CatMeta): Unit =
    if (isDifferent(p, c)) {
      val inheritedMeta = inheritMeta(p, c)
      val metaFile = path.resolve(DirectoryReadWriteConfig.categoryMetaFileName)
  
      writeMetaToFile(metaFile, inheritedMeta)
      writeContent(path, p, c)
    }
  
  def writeIfDifferent(path: Path, p: CatMeta, c: WfMeta): Unit =
    if (isDifferent(p, c)) {
      val wfFolder = createWfFolder(path, c)
      val inheritedMeta = inheritMeta(p, c)
      val metaFile = wfFolder.resolve(DirectoryReadWriteConfig.workflowMetaFileName)
  
      writeMetaToFile(metaFile, inheritedMeta)
      writeContent(wfFolder, p, c)
    } else {
      writeNewContent(path, Map.empty, c.sql)
    }
  
  private def inheritMeta(p: CatMeta, c: CatMeta): CategoryFileFields =
    CategoryFileFields(
      grenki   = MetaFieldsComparer.diffField(p.grenkiVersion, c.grenkiVersion),
      queue    = MetaFieldsComparer.diffField(p.queue, c.queue),
      user     = MetaFieldsComparer.diffField(p.user, c.user),
      init     = Some(MetaFieldsComparer.diffMapKeyset(p.init, c.init).toList),
      map      = Some(MetaFieldsComparer.diffMapKeyset(p.sqlMap, c.sqlMap).toList),
      param    = Some(MetaFieldsComparer.diffMap(p.params, c.params)),
      stats    = Some(MetaFieldsComparer.diffSet(p.stats, c.stats).map(_.toInt)),
      entities = Some(MetaFieldsComparer.diffSet(p.entities, c.entities).map(_.toInt))
  )
  
  private def inheritMeta(p: CatMeta, c: WfMeta): WorkflowFileFields =
    WorkflowFileFields(
      grenki   = MetaFieldsComparer.diffField(p.grenkiVersion, c.grenkiVersion),
      queue    = MetaFieldsComparer.diffField(p.queue, c.queue),
      user     = MetaFieldsComparer.diffField(p.user, c.user),
      init     = Some(MetaFieldsComparer.diffMapKeyset(p.init, c.init).toList),
      map      = Some(MetaFieldsComparer.diffMapKeyset(p.sqlMap, c.sqlMap).toList),
      param    = Some(MetaFieldsComparer.diffMap(p.params, c.params)),
      stats    = Some(MetaFieldsComparer.diffSet(p.stats, c.stats).map(_.toInt)),
      sql      = Some(c.sql.keySet),
      entities = Some(MetaFieldsComparer.diffSet(p.entities, c.entities).map(_.toInt))
    )
  
  private def writeContent[T](path: Path, p: T, c: T): Unit = (p, c) match {
    case (p: CatMeta, c: CatMeta) =>
      writeNewContent(path, p.sqlMap, c.sqlMap)
      writeNewContent(path, p.init, c.init)
    case (p: CatMeta, c: WfMeta) =>
      writeNewContent(path, p.sqlMap, c.sqlMap)
      writeNewContent(path, p.init, c.init)
      writeNewContent(path, Map.empty, c.sql)
  }
  
  def writeNewContent(path: Path, p: Map[String, String], c: Map[String, String]): Unit = {
    val newContent = (c.toSet diff p.toSet).toMap
    for ((fileName, fileContent) <- newContent) {
      writeTextFileToDirectory(fileName, fileContent, path)
    }
  }
  
  private def isDifferent(p: CatMeta, c: CatMeta): Boolean =
    c.copy(name = p.name) != p
  
  private def isDifferent(p: CatMeta, c: WfMeta): Boolean =
    c.sql.size != 1 && p != CategoryMetaDefault(
      name = p.name,
      sqlMap = c.sqlMap,
      init = c.init,
      user = c.user,
      queue = c.queue,
      grenkiVersion = c.grenkiVersion,
      params = c.params,
      stats = c.stats,
      entities = c.entities
    )
  
  def createWfFolder(path: Path, c: WfMeta): Path = {
    val wfFolder = path.resolve(DirectoryReadWriteConfig.workflowFolderPrefix + c.name)
    Files.createDirectories(wfFolder)
  }
}
