package ru.sber.cb.ap.gusli.actor.projects.write

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

import ru.sber.cb.ap.gusli.actor.core.dto.WorkflowDto
import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, CategoryMetaDefault, EntityMeta, ProjectMeta}
import ru.sber.cb.ap.gusli.actor.projects.DirectoryReadWriteConfig
import ru.sber.cb.ap.gusli.actor.projects.write.MetaToYamlSerialization._

object MetaToHDD {
  def entityRoot(path: Path, name: String): Path =
    createNewFolder(name, path)

  def writeProjectMetaToPath(meta: ProjectMeta, path: Path): Path =
    createNewFolder(meta.name, path)
  
  def writeCategoryMetaToPath(meta: CategoryMeta, parentDir: Path, parentMeta: CategoryMeta): Path = {
    val categoryFolder: Path = createNewFolder(meta.name, parentDir)
    
    val child = meta.asInstanceOf[CategoryMetaDefault]
    val parent = parentMeta.asInstanceOf[CategoryMetaDefault]
    
    if (child.copy(name = parent.name) != parent) {
      for ((fileName, fileContent) <- getFilesContentFromCategory(child, parent)) {
        writeYAMLTextFileToDirectory(fileName, fileContent, categoryFolder)
      }
    }
    categoryFolder
  }
  
  
  def writeWorkflowMetaToPath(dto: WorkflowDto, dir: Path, categoryMeta: CategoryMeta): Either[Path,Path] = {
    val fileContent = convertWorkflowMetaToYAMLFileContent(dto,categoryMeta)

//    val workflowFolder = createNewFolder(dto.name, dir)
    val wriitenFile = writeYAMLTextFileToDirectory(s"wf-${dto.name}.yaml", fileContent, dir)


    Left(wriitenFile)
  }
  
  
  def writeEntityMetaToPath(meta: EntityMeta, dir: Path, childrenCount: Int): Either[Path,Path] = {
    val fileContent = convertEntityMetaToYAMLFileContent(meta)
    val entityNameOnHDD = s"${meta.id} ${meta.name}"
    childrenCount match {
      case 0 => Left(writeYAMLTextFileToDirectory(s"$entityNameOnHDD.yaml", fileContent, dir))
      case _ =>
        val entityFolder = createNewFolder(entityNameOnHDD, dir)
        writeYAMLTextFileToDirectory(DirectoryReadWriteConfig.entityMetaFileName, fileContent, entityFolder)
        Right(entityFolder)
    }
  }
  
  
  private val charSet = StandardCharsets.UTF_8
  
  
  private def createNewFolder(newFolderName: String, dirWhereWillBeCreatedNewFolder: Path): Path = {
    Files.createDirectories(dirWhereWillBeCreatedNewFolder.resolve(normalizeName(newFolderName)))
  }
  
//  private def createNewFile(name: String, path: Path): Path = {
//    Files.createDirectories(path)
//    Files.write(path, )
//  }
  
  private def normalizeName(folderName: String): String = {
    folderName.replace("/", "~").replace(":", "%3A").trim
//    val tt = folderName.replace("/", "~").replace(":", "%3A").trim
//    URLEncoder.encode(folderName, charSet.toString)
  }
  
  private def writeYAMLTextFileToDirectory(fileName: String, fileContent: String, dir: Path): Path =
    Files.write(dir.resolve(fileName), fileContent.getBytes(charSet))
}
