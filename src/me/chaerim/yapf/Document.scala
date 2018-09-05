package me.chaerim.yapf

import java.nio.file.{Files, Paths}

import com.intellij.notification.NotificationType
import com.intellij.openapi.editor.{Document => IdeaDocument}
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.{ProjectManager, Project => IdeaProject}
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcsUtil.VcsUtil
import me.chaerim.yapf.Project._
import me.chaerim.yapf.Result.NotFoundExecutable
import me.chaerim.yapf.Util._

case class Document(document: IdeaDocument) {
  private val virtualFile: VirtualFile = FileDocumentManager.getInstance.getFile(document)

  private val project: Option[IdeaProject] = ProjectManager.getInstance.getOpenProjects
    .find(ProjectRootManager.getInstance(_).getFileIndex.isInContent(virtualFile))

  val projectSettings: Option[Settings] = project.map(_.settings)

  val isFormattable: Boolean = virtualFile.getFileType.getName.compareToIgnoreCase("Python") == 0 &&
    virtualFile.getExtension.compareToIgnoreCase("py") == 0

  private def findExecutable: Either[Result, String] =
    (for {
      maybeExecutable <- List(projectSettings.map(_.executablePath), Option(Settings.DefaultExecutablePath)).distinct
      executable      <- maybeExecutable
      if Files.exists(Paths.get(executable))
    } yield executable).headOption.toRight(NotFoundExecutable)

  private def findConfigFile: Option[String] =
    (for {
      maybeConfigFile <- List(projectSettings.map(_.styleFileName), Option(Settings.DefaultStyleFileName)).distinct
      maybeDirectory  <- List(project.map(_.getBasePath), project.map(VcsUtil.getVcsRootFor(_, virtualFile).getPath)).distinct
      directory       <- maybeDirectory
      configFile      <- maybeConfigFile
      fullPath = Paths.get(directory, configFile)
      if Files.exists(fullPath)
    } yield fullPath.toAbsolutePath.toString).headOption

  def format: Unit = if (isFormattable) {
    (for {
      executable    <- findExecutable
      formattedCode <- runYapfCommand(executable, document.getText, findConfigFile)
    } yield formattedCode) match {
      case Right(formattedCode) => setFormattedCode(document, formattedCode)
      case Left(result) =>
        notifyMessage(s"${result.message}\n${result.detail.getOrElse("")}".stripMargin, NotificationType.ERROR)
    }
  }
}
