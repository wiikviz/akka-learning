package ru.sber.cb.ap.gusli.actor.projects.writeres

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

import ru.sber.cb.ap.gusli.actor.core.dto.WorkflowDto
import ru.sber.cb.ap.gusli.actor.core.{CategoryMeta, EntityMeta, ProjectMeta}
import ru.sber.cb.ap.gusli.actor.projects.writeres.MetaToYamlSerialization._

object MetaToHDD {
  def writeProjectMetaToPath(meta: ProjectMeta, path: Path): Path =
    createNewFolder(meta.name, path)


  def writeCategoryMetaToPath(meta: CategoryMeta, dir: Path, parentMeta: CategoryMeta): Path = {
    val categoryFolder = createNewFolder(meta.name, dir)


    categoryFolder
  }


  def writeWorkflowMetaToPath(dto: WorkflowDto, dir: Path, categoryMeta: CategoryMeta): Either[Path,Path] = {
    val fileContent = convertWorkflowMetaToYAMLFileContent(dto,categoryMeta)

//    val workflowFolder = createNewFolder(dto.name, dir)
    val wriitenFile = writeYAMLTextFileToDirectory(fileContent, s"wf-${dto.name}", dir)


    Left(wriitenFile)
  }


  def writeEntityMetaToPath(meta: EntityMeta, dir: Path, parentMeta: EntityMeta, hasChildren: Boolean): Either[Path,Path] = {
    val fileContent = convertEntityMetaToYAMLFileContent(meta, parentMeta)
    val entityNameOnHDD = s"${meta.id} ${meta.id}"
    if (hasChildren) {
      val entityFolder = createNewFolder(entityNameOnHDD, dir)
      writeYAMLTextFileToDirectory(fileContent, "entity", entityFolder)
      Right(entityFolder)
    }
    else
      Left(writeYAMLTextFileToDirectory(fileContent, entityNameOnHDD, dir))
  }


  private val charSet = StandardCharsets.UTF_8

  private def createNewFolder(newFolderName: String, dirWereWillBeCreatedNewFolder: Path): Path = {
    Files.createDirectories(dirWereWillBeCreatedNewFolder resolve normalizeName(newFolderName))
  }

  private def normalizeName(folderName: String): String = {
    val tt = folderName.replace("/", "~").replace(":", "%3A").trim
    URLEncoder.encode(folderName, charSet.toString)
  }

  private def writeYAMLTextFileToDirectory(fileContent: String, fileName: String, dir: Path): Path = {
    Files.write(dir.resolve(fileName + ".yaml"), fileContent.getBytes(charSet))
  }
}
