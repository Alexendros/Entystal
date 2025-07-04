package entystal.viewmodel

import scalafx.beans.property.StringProperty
import scalafx.beans.binding.{BooleanBinding, Bindings}
import entystal.service.RegistroService

/** ViewModel para el formulario de registro */
class RegistroViewModel(service: RegistroService, validator: RegistroValidator) {
  val tipo          = StringProperty("activo")
  val identificador = StringProperty("")
  val descripcion   = StringProperty("")

  /** Validación reactiva de los campos */
  val puedeRegistrar: BooleanBinding = Bindings.createBooleanBinding(
    () => validator.validate(RegistroData(tipo.value, identificador.value, descripcion.value)).isRight,
    identificador,
    descripcion,
    tipo,
  )

  /** Devuelve mensaje de error o confirma el registro */
  def registrar(): String =
    validator.validate(RegistroData(tipo.value, identificador.value, descripcion.value)) match {
      case Left(err) => err
      case Right(_)  => service.registrar(RegistroData(tipo.value, identificador.value, descripcion.value))
    }
}
