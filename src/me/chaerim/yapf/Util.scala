package me.chaerim.yapf

import java.io.ByteArrayInputStream

import com.intellij.notification.Notifications.Bus
import com.intellij.notification.{Notification, NotificationType}
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.{Document => IdeaDocument}
import me.chaerim.yapf.Result.{FailedToRunCommand, IllegalYapfResult}

import scala.sys.process._
import scala.util.{Failure, Success, Try}

object Util {
  val DefaultCharset: String = "utf-8"

  def notifyMessage(context: String, notificationType: NotificationType): Unit =
    Bus.notify(new Notification(Settings.PluginName, Settings.PluginName, context, notificationType))

  def makeYapfCommand(executable: String, configFile: Option[String]): List[String] =
    executable :: configFile.toList.flatMap(List("--style", _))

  def runYapfCommand(executable: String, code: String, configFile: Option[String]): Either[Result, String] = {
    val stdout: StringBuilder = new StringBuilder
    val stderr: StringBuilder = new StringBuilder

    Try {
      val source: ByteArrayInputStream = new ByteArrayInputStream(code.getBytes(DefaultCharset))

      val processLogger: ProcessLogger = ProcessLogger(
        (s: String) => stdout.appendAll(s + "\n"),
        (s: String) => stderr.appendAll(s + "\n")
      )

      makeYapfCommand(executable, configFile) #< source ! processLogger
    } match {
      case Success(s) if s != 0 => Left(IllegalYapfResult(Some(stderr.toString)))
      case Success(_)           => Right(stdout.toString)
      case Failure(e)           => Left(FailedToRunCommand(Some(e.toString)))
    }
  }

  def setFormattedCode(ideaDocument: IdeaDocument, formattedCode: String): Unit =
    ApplicationManager.getApplication.runWriteAction(new Runnable {
      override def run(): Unit =
        CommandProcessor.getInstance.runUndoTransparentAction(() => ideaDocument.setText(formattedCode))
    })
}
