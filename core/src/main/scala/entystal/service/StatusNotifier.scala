package entystal.service

import scalafx.scene.control.Label
import scalafx.application.Platform

/** Notificador que muestra mensajes en una barra de estado y delega en DialogNotifier */
class StatusNotifier(statusLabel: Label) extends Notifier {
  override def success(msg: String): Unit = {
    Platform.runLater(statusLabel.text = msg)
    DialogNotifier.success(msg)
  }
  override def error(msg: String): Unit   = {
    Platform.runLater(statusLabel.text = msg)
    DialogNotifier.error(msg)
  }
  override def warning(msg: String): Unit = {
    Platform.runLater(statusLabel.text = msg)
    DialogNotifier.warning(msg)
  }
}
