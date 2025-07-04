package entystal.viewmodel

import scalafx.beans.property.StringProperty
import scalafx.beans.binding.{BooleanBinding, Bindings}
import entystal.model._
import entystal.service.RegistroService

import entystal.i18n.I18n

/** ViewModel para el formulario de registro */
class RegistroViewModel(service: RegistroService, validator: RegistroValidator) {
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

  /** Devuelve mensaje de error o confirma el registro */
  def registrar(): String = {
    if (!puedeRegistrar.value) {
      if (identificador.value.trim.isEmpty)
        return I18n("error.idRequerido")
      if (descripcion.value.trim.isEmpty)
        return if (tipo.value == "inversion") I18n("error.cantidadRequerida")
        else I18n("error.descripcionRequerida")
      if (tipo.value == "inversion" && !descripcion.value.matches("^\\d+(\\.\\d+)?$"))
        return I18n("error.cantidadNumerica")
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
