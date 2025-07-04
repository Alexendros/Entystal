package entystal.viewmodel

import scalafx.beans.property.StringProperty
import scalafx.beans.binding.{BooleanBinding, Bindings}
import entystal.model._
import entystal.service.{RegistroService, Notifier}
import entystal.viewmodel.RegistroValidator
import zio.Runtime

/** ViewModel para el formulario de registro */
class RegistroViewModel(
    service: RegistroService,
    validator: RegistroValidator,
    notifier: Notifier
)(implicit runtime: Runtime[Any]) {

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

    val ts = System.currentTimeMillis
    tipo.value match {
      case "activo" =>
        val asset = DataAsset(identificador.value, descripcion.value, ts, BigDecimal(1))
        zio.Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(service.registrarActivo(asset)).getOrThrow()
        }
      case "pasivo" =>
        val liability = EthicalLiability(identificador.value, descripcion.value, ts, BigDecimal(1))
        zio.Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(service.registrarPasivo(liability)).getOrThrow()
        }
      case _        =>
        val qty        = BigDecimal(descripcion.value)
        val investment = BasicInvestment(identificador.value, qty, ts)
        zio.Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(service.registrarInversion(investment)).getOrThrow()
        }
    }
    notifier.success("Registro completado")
  }
}
