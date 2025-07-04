package entystal.viewmodel

/** Datos introducidos en el formulario de registro */
final case class RegistroData(tipo: String, identificador: String, descripcion: String)

/** Encapsula la l\u00f3gica de validaci\u00f3n para el registro */
class RegistroValidator {

  /** Valida el conjunto de datos.
    * @return
    *   Right(()) si es correcto o Left(mensaje de error)
    */
  def validate(data: RegistroData): Either[String, Unit] = {
    if (data.identificador.trim.isEmpty) Left("ID requerido")
    else if (data.descripcion.trim.isEmpty)
      Left(if (data.tipo == "inversion") "Cantidad requerida" else "Descripci\u00f3n requerida")
    else if (data.tipo == "inversion" && !data.descripcion.matches("^\\d+(\\.\\d+)?$"))
      Left("La cantidad debe ser num\u00e9rica")
    else Right(())
  }
}
