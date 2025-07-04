package entystal.service

import entystal.ledger.Ledger
import entystal.model._
import entystal.viewmodel.RegistroData
import zio.Runtime

/** Servicio que registra activos, pasivos o inversiones en el ledger */
class RegistroService(ledger: Ledger)(implicit runtime: Runtime[Any]) {
  /** Realiza el registro y devuelve un mensaje de confirmaci\u00f3n */
  def registrar(data: RegistroData): String = {
    val ts = System.currentTimeMillis()
    data.tipo match {
      case "activo" =>
        val asset = DataAsset(data.identificador, data.descripcion, ts, BigDecimal(1))
        zio.Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(ledger.recordAsset(asset)).getOrThrow()
        }
      case "pasivo" =>
        val liability = EthicalLiability(data.identificador, data.descripcion, ts, BigDecimal(1))
        zio.Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(ledger.recordLiability(liability)).getOrThrow()
        }
      case _ =>
        val qty = BigDecimal(data.descripcion)
        val investment = BasicInvestment(data.identificador, qty, ts)
        zio.Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(ledger.recordInvestment(investment)).getOrThrow()
        }
    }
    "Registro completado"
  }
}
