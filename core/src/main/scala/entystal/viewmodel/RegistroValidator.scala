package entystal.viewmodel

import entystal.InputValidator

/** Datos introducidos en el formulario de registro */
final case class RegistroData(tipo: String, identificador: String, descripcion: String)

/** Encapsula la l\u00f3gica de validaci\u00f3n para el registro */
class RegistroValidator extends InputValidator[RegistroData] {

  /** Delegamos en el validador compartido */
  def validate(data: RegistroData): Either[String, Unit] =
    entystal.InputValidators.RegistroDataValidator.validate(data)
}
