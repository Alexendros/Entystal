package entystal.service

import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

/** Servicio para mostrar notificaciones modales. */
trait Notifier {
  def success(msg: String): Unit
  def error(msg: String): Unit
  def warning(msg: String): Unit
}

/** Implementación basada en cuadros de diálogo de ScalaFX. */
object DialogNotifier extends Notifier {
  override def success(msg: String): Unit =
    show(AlertType.Information, "Éxito", msg)

  override def error(msg: String): Unit =
    show(AlertType.Error, "Error", msg)

  override def warning(msg: String): Unit =
    show(AlertType.Warning, "Advertencia", msg)

  private def show(alertType: AlertType, titleStr: String, msg: String): Unit = {
    val alert = new Alert(alertType) {
      headerText = null: String
      title = titleStr
      contentText = msg
    }
    alert.showAndWait()
    ()
  }
}
