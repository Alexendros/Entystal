package entystal.viewmodel

import scalafx.beans.property.{StringProperty, ObjectProperty}
import scalafx.beans.binding.{BooleanBinding, Bindings}
import entystal.model._
import entystal.ledger.Ledger
import zio.Runtime
import entystal.i18n.I18n

/** ViewModel para el formulario de registro */
class RegistroViewModel(ledger: Ledger)(implicit runtime: Runtime[Any]) {
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
    I18n("mensaje.registroCompletado")
  }
}
