package me.chaerim.yapf

import java.nio.file.{Files, Paths}

import com.intellij.notification.NotificationType
import com.intellij.openapi.editor.{Document => IdeaDocument}
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.{Project, ProjectManager}
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcsUtil.VcsUtil
import me.chaerim.yapf.Result.{AlreadyFormattedCode, NotFoundExecutable, UnformattableFile}
import me.chaerim.yapf.Util._

case class Document(document: IdeaDocument) {
  private val virtualFileOfDocument: Option[VirtualFile] = Option(FileDocumentManager.getInstance.getFile(document))

  private val projectOfDocument: Option[Project] = virtualFileOfDocument.flatMap { virtualFile =>
    ProjectManager.getInstance.getOpenProjects.find { project =>
      ProjectRootManager.getInstance(project).getFileIndex.isInContent(virtualFile)
    }
  }

  val settings: Option[Settings] = projectOfDocument.map(Settings(_))

  val isFormattable: Boolean = virtualFileOfDocument.exists { virtualFile =>
    virtualFile.getFileType.getName.compareToIgnoreCase("Python") == 0 &&
    virtualFile.getExtension.compareToIgnoreCase("py") == 0
  }

  private def findExecutable: Either[Result, String] =
    (for {
      maybeExecutable <- List(settings.map(_.executablePath), Option(Settings.DefaultExecutablePath)).distinct
      executable      <- maybeExecutable
      if Files.exists(Paths.get(executable))
    } yield executable).headOption.toRight(NotFoundExecutable)

  private def findConfigFile: Option[String] = {
    (for {
      project         <- projectOfDocument.toList
      maybeConfigFile <- List(settings.map(_.styleFileName), Option(Settings.DefaultStyleFileName)).distinct
      maybeDirectory <- List(Option(project.getBasePath),
                             virtualFileOfDocument.map(VcsUtil.getVcsRootFor(project, _).getPath)).distinct
      directory  <- maybeDirectory
      configFile <- maybeConfigFile
      fullConfigFilePath = Paths.get(directory, configFile)
      if Files.exists(fullConfigFilePath)
    } yield fullConfigFilePath.toAbsolutePath.toString).headOption
  }

  def format: Unit =
    (for {
      executable <- findExecutable
      _          <- Either.cond(isFormattable, "", UnformattableFile)
      configFile   = findConfigFile
      originalCode = document.getText
      formattedCode <- runYapfCommand(executable,                 originalCode,  configFile)
      result        <- Either.cond(originalCode != formattedCode, formattedCode, AlreadyFormattedCode)
    } yield result) match {
      case Right(formattedCode) => setFormattedCode(document, formattedCode)
      case Left(result) if result.shouldNotify =>
        notifyMessage(s"${result.message}\n${result.detail.getOrElse("")}".stripMargin, NotificationType.ERROR)
    }
}
