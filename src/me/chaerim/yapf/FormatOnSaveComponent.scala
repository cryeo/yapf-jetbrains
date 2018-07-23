package me.chaerim.yapf

import com.intellij.AppTopics
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.editor.{Document => IdeaDocument}
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter

class FormatOnSaveComponent extends ApplicationComponent {
  override def getComponentName: String = s"${Settings.PluginName}.FormatOnSave"

  private val fileDocumentManagerAdapter: FileDocumentManagerAdapter =
    new FileDocumentManagerAdapter {
      override def beforeDocumentSaving(ideaDocument: IdeaDocument): Unit =
        for {
          document <- Option(Document(ideaDocument))
          settings <- document.settings
          if settings.formatOnSave
        } yield document.format
    }

  override def initComponent(): Unit =
    ApplicationManager.getApplication.getMessageBus.connect
      .subscribe(AppTopics.FILE_DOCUMENT_SYNC, fileDocumentManagerAdapter)

  override def disposeComponent(): Unit = ()
}
