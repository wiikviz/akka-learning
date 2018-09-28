package ru.sber.cb.ap.gusli.actor.projects.write

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

import ru.sber.cb.ap.gusli.actor.core.dto.WorkflowDto
import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, CategoryMetaDefault, EntityMeta, ProjectMeta}
import ru.sber.cb.ap.gusli.actor.projects.DirectoryReadWriteConfig
import ru.sber.cb.ap.gusli.actor.projects.write.MetaToYamlSerialization._

object MetaToHDD {
  def writeProjectMetaToPath(meta: ProjectMeta, path: Path): Path =
    createNewFolder(meta.name, path)


  def writeCategoryMetaToPath(meta: CategoryMeta, dir: Path, parentMeta: CategoryMeta): Path = {
    val categoryFolder: Path = createNewFolder(meta.name, dir)

    val child = meta.asInstanceOf[CategoryMetaDefault]
    val parent = parentMeta.asInstanceOf[CategoryMetaDefault]

    if(child.copy(name = parent.name) != parent){
      for( (fileName, fileContent) <- getFilesContentFromCategory(child,parent)){
        writeYAMLTextFileToDirectory(fileName,fileContent, categoryFolder)
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


  def writeEntityMetaToPath(meta: EntityMeta, dir: Path, parentMeta: EntityMeta, hasChildren: Boolean): Either[Path,Path] = {
    val fileContent = convertEntityMetaToYAMLFileContent(meta, parentMeta)
    val entityNameOnHDD = s"${meta.id} ${meta.id}"
    if (hasChildren) {
      val entityFolder = createNewFolder(entityNameOnHDD, dir)
      writeYAMLTextFileToDirectory(DirectoryReadWriteConfig.entityMetaFileName, fileContent, entityFolder)
      Right(entityFolder)
    }
    else
      Left(writeYAMLTextFileToDirectory(s"$entityNameOnHDD.yaml", fileContent, dir))
  }


  private val charSet = StandardCharsets.UTF_8

  private def createNewFolder(newFolderName: String, dirWereWillBeCreatedNewFolder: Path): Path = {
    Files.createDirectories(dirWereWillBeCreatedNewFolder resolve normalizeName(newFolderName))
  }

  private def normalizeName(folderName: String): String = {
    val tt = folderName.replace("/", "~").replace(":", "%3A").trim
    URLEncoder.encode(folderName, charSet.toString)
  }

  private def writeYAMLTextFileToDirectory(fileName:String, fileContent: String, dir: Path): Path =
    Files.write(dir.resolve(fileName), fileContent.getBytes(charSet))
}
