package entystal.viewmodel

import scalafx.beans.property.StringProperty
import scalafx.beans.binding.{BooleanBinding, Bindings}
import entystal.model._
import entystal.ledger.Ledger
import entystal.service.Notifier
import zio.Runtime

/** ViewModel para el formulario de registro */
class RegistroViewModel(ledger: Ledger, notifier: Notifier)(implicit
    runtime: Runtime[Any]
) {
  val tipo          = StringProperty("activo")
  val identificador = StringProperty("")
  val descripcion   = StringProperty("")

  /** Validación reactiva de los campos */
  val puedeRegistrar: BooleanBinding = Bindings.createBooleanBinding(
    () => {
      val idOk   = identificador.value.trim.nonEmpty
      val descOk = descripcion.value.trim.nonEmpty
      val qtyOk  = !tipo.value.equalsIgnoreCase("inversion") ||
        descripcion.value.matches("^\\d+(\\.\\d+)?$")
      idOk && descOk && qtyOk
    },
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

    val ts = System.currentTimeMillis
    tipo.value match {
      case "activo" =>
        val asset = DataAsset(identificador.value, descripcion.value, ts, BigDecimal(1))
        zio.Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(ledger.recordAsset(asset)).getOrThrow()
        }
      case "pasivo" =>
        val liability = EthicalLiability(identificador.value, descripcion.value, ts, BigDecimal(1))
        zio.Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(ledger.recordLiability(liability)).getOrThrow()
        }
      case _        =>
        val qty        = BigDecimal(descripcion.value)
        val investment = BasicInvestment(identificador.value, qty, ts)
        zio.Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(ledger.recordInvestment(investment)).getOrThrow()
        }
    }
    notifier.success("Registro completado")
  }
}
