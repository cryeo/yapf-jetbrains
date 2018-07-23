package me.chaerim.yapf

import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent, CommonDataKeys}
import com.intellij.openapi.fileEditor.FileEditorManager

class FormatAction extends AnAction {
  override def actionPerformed(event: AnActionEvent): Unit =
    for {
      currentProject  <- Option(event.getData(CommonDataKeys.PROJECT))
      currentEditor   <- Option(FileEditorManager.getInstance(currentProject).getSelectedTextEditor)
      currentDocument <- Option(currentEditor.getDocument)
      document        <- Option(Document(currentDocument))
    } yield document.format
}
