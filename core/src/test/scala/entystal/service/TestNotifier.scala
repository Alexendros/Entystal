package entystal.service

/** Implementación de Notifier que acumula las llamadas para pruebas. */
class TestNotifier extends Notifier {
  var last: Option[(String, String)]      = None
  override def success(msg: String): Unit =
    last = Some("success" -> msg)
  override def error(msg: String): Unit   =
    last = Some("error" -> msg)
  override def warning(msg: String): Unit =
    last = Some("warning" -> msg)
}
