package entystal.viewmodel

import scalafx.beans.property.StringProperty
import scalafx.beans.binding.{BooleanBinding, Bindings}
import entystal.model._
import entystal.service.{RegistroService, Notifier}
import zio.Runtime

/** ViewModel para el formulario de registro */
class RegistroViewModel(
    service: RegistroService,
    notifier: Notifier,
    validator: RegistroValidator = new RegistroValidator
)(implicit runtime: Runtime[Any]) {

  val tipo          = StringProperty("activo")
  val identificador = StringProperty("")
  val descripcion   = StringProperty("")

  /** Validación reactiva de los campos */
  val puedeRegistrar: BooleanBinding = Bindings.createBooleanBinding(
    () =>
      validator.validate(RegistroData(tipo.value, identificador.value, descripcion.value)).isRight,
    identificador,
    descripcion,
    tipo
  )

  /** Ejecuta el registro mostrando el resultado mediante el notifier */
  def registrar(): Unit = {
    if (!puedeRegistrar.value) {
      if (identificador.value.trim.isEmpty) {
        notifier.error("ID requerido")
        return
      }
      if (descripcion.value.trim.isEmpty) {
        notifier.error(
          if (tipo.value == "inversion") "Cantidad requerida" else "Descripción requerida"
        )
        return
      }
      if (tipo.value == "inversion" && !descripcion.value.matches("^\\d+(\\.\\d+)?$")) {
        notifier.error("La cantidad debe ser numérica")
        return
      }
    }

    val data = RegistroData(tipo.value, identificador.value, descripcion.value)
    service.registrar(data)
  }

  /** Exporta el historial a CSV y devuelve mensaje de confirmación */
  def exportCsv(path: String = "ledger.csv"): String = {
    service.exportCsv(path)
  }

  /** Exporta el historial a PDF y devuelve mensaje de confirmación */
  def exportPdf(path: String = "ledger.pdf"): String = {
    service.exportPdf(path)
  }
}
