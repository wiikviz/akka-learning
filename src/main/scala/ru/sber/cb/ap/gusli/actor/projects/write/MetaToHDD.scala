package ru.sber.cb.ap.gusli.actor.projects.write

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

import ru.sber.cb.ap.gusli.actor.core.dto.WorkflowDto
import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, CategoryMetaDefault, EntityMeta, ProjectMeta}
import ru.sber.cb.ap.gusli.actor.projects.DirectoryReadWriteConfig
import ru.sber.cb.ap.gusli.actor.projects.yamlfiles.YamlFileMapperWrite

object MetaToHDD {
  
  private val charSet = StandardCharsets.UTF_8
  
  def entityRoot(path: Path, name: String): Path =
    createNewFolder(name, path)

  def writeProjectMetaToPath(meta: ProjectMeta, path: Path): Path =
    createNewFolder(meta.name, path)
  
  def writeCategoryMetaToPath(meta: CategoryMeta, parentDir: Path, parentMeta: CategoryMeta): Path = {
    val categoryFolder: Path = createNewFolder(meta.name, parentDir)
    val child = meta.asInstanceOf[CategoryMetaDefault]
    val parent = parentMeta.asInstanceOf[CategoryMetaDefault]
  
    YamlFileMapperWrite.writeMeta(categoryFolder, parent, child)
    categoryFolder
  }
  
  def writeWorkflowMetaToPath(wfDto: WorkflowDto, dir: Path, categoryMeta: CategoryMeta): Unit =
    YamlFileMapperWrite.writeMeta(dir, categoryMeta.asInstanceOf[CategoryMetaDefault], wfDto)
  
  def writeEntityMetaToPath(meta: EntityMeta, dir: Path, childrenCount: Int): Either[Path,Path] = {
    val fileContent = MetaToYamlSerialization.convertEntityMetaToYAMLFileContent(meta)
    val entityNameOnHDD = s"${meta.id} ${meta.name}"
    childrenCount match {
      case 0 => Left(writeYAMLTextFileToDirectory(s"$entityNameOnHDD.yaml", fileContent, dir))
      case _ =>
        val entityFolder = createNewFolder(entityNameOnHDD, dir)
        writeYAMLTextFileToDirectory(DirectoryReadWriteConfig.entityMetaFileName, fileContent, entityFolder)
        Right(entityFolder)
    }
  }
  
  private def createNewFolder(newFolderName: String, dirWhereWillBeCreatedNewFolder: Path): Path = {
    Files.createDirectories(dirWhereWillBeCreatedNewFolder.resolve(normalizeName(newFolderName)))
  }

  private def normalizeName(folderName: String): String = {
    folderName.replace("/", "~").replace(":", "%3A").trim
//    val tt = folderName.replace("/", "~").replace(":", "%3A").trim
//    URLEncoder.encode(folderName, charSet.toString)
  }
  
  private def writeYAMLTextFileToDirectory(fileName: String, fileContent: String, dir: Path): Path =
    Files.write(dir.resolve(fileName), fileContent.getBytes(charSet))
}
