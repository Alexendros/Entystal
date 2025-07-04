package entystal.service

import entystal.ledger.{Ledger, LedgerEntry}
import entystal.model._
import entystal.viewmodel.RegistroData
import entystal.util.{CsvExporter, PdfExporter}
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
      case _        =>
        val qty        = BigDecimal(data.descripcion)
        val investment = BasicInvestment(data.identificador, qty, ts)
        zio.Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(ledger.recordInvestment(investment)).getOrThrow()
        }
    }
    "Registro completado"
  }

  /** Obtiene todo el historial registrado */
  def history: List[LedgerEntry] =
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(ledger.getHistory).getOrThrow()
    }

  /** Exporta el historial completo a CSV */
  def exportCsv(path: String): String = {
    val entries = history
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(CsvExporter.save(entries, path)).getOrThrow()
    }
    s"CSV exportado en $path"
  }

  /** Exporta el historial completo a PDF */
  def exportPdf(path: String): String = {
    val entries = history
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(PdfExporter.save(entries, path)).getOrThrow()
    }
    s"PDF exportado en $path"
  }
}
