package entystal

/** Componente genérico de validación de entradas */
trait InputValidator[T] {

  /** Valida el valor indicado
    * @return
    *   Right(()) si es válido o Left(mensaje de error)
    */
  def validate(value: T): Either[String, Unit]
}

/** Validaciones disponibles */
object InputValidators {
  import entystal.viewmodel.RegistroData

  /** Validador para los datos de registro */
  object RegistroDataValidator extends InputValidator[RegistroData] {
    def validate(data: RegistroData): Either[String, Unit] = {
      if (data.identificador.trim.isEmpty) Left("ID requerido")
      else if (data.descripcion.trim.isEmpty)
        Left(if (data.tipo == "inversion") "Cantidad requerida" else "Descripción requerida")
      else if (data.tipo == "inversion" && !data.descripcion.matches("^\\d+(\\.\\d+)?$"))
        Left("La cantidad debe ser numérica")
      else Right(())
    }
  }
}
