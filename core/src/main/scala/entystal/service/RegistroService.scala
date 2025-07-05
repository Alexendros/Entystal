package entystal.service

import entystal.ledger.{Ledger, LedgerEntry, AssetEntry, LiabilityEntry, InvestmentEntry}
import entystal.model._
import entystal.viewmodel.RegistroData
import entystal.util.{CsvExporter, PdfExporter}
import zio.UIO

/** Servicio que registra y consulta el ledger */
class RegistroService(private val ledger: Ledger) {
  private val runtime = zio.Runtime.default

  /** Registra un activo, pasivo o inversión y devuelve un mensaje de confirmación */
  def registrar(data: RegistroData): String = {
    val ts = System.currentTimeMillis()
    data.tipo match {
      case "activo" =>
        val asset = DataAsset(data.identificador, data.descripcion, ts, BigDecimal(1))
        zio.Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(ledger.recordAsset(asset)).getOrThrow()
        }
      case "pasivo" =>
        val liab = EthicalLiability(data.identificador, data.descripcion, ts, BigDecimal(1))
        zio.Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(ledger.recordLiability(liab)).getOrThrow()
        }
      case _        =>
        val qty = BigDecimal(data.descripcion)
        val inv = BasicInvestment(data.identificador, qty, ts)
        zio.Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(ledger.recordInvestment(inv)).getOrThrow()
        }
    }
    "Registro completado"
  }

  /** Obtiene todo el historial registrado */
  def history: List[LedgerEntry] =
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(ledger.getHistory).getOrThrow()
    }

  /** Suma los totales agregados de activos, pasivos e inversiones */
  def aggregateTotals(): UIO[(BigDecimal, BigDecimal, BigDecimal)] =
    ledger.getHistory.map { entries =>
      entries.foldLeft((BigDecimal(0), BigDecimal(0), BigDecimal(0))) {
        case ((aAcc, lAcc, iAcc), AssetEntry(a))        => (aAcc + a.value, lAcc, iAcc)
        case ((aAcc, lAcc, iAcc), LiabilityEntry(l))    => (aAcc, lAcc + l.amount, iAcc)
        case ((aAcc, lAcc, iAcc), InvestmentEntry(inv)) => (aAcc, lAcc, iAcc + inv.quantity)
      }
    }

  /** Exporta el historial completo a CSV y devuelve la ruta */
  def exportCsv(path: String): String = {
    val entries = history
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(CsvExporter.save(entries, path)).getOrThrow()
    }
    s"CSV exportado en $path"
  }

  /** Exporta el historial completo a PDF y devuelve la ruta */
  def exportPdf(path: String): String = {
    val entries = history
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(PdfExporter.save(entries, path)).getOrThrow()
    }
    s"PDF exportado en $path"
  }
}
