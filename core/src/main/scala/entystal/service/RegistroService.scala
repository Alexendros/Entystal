package entystal.service

import entystal.ledger.{AssetEntry, InvestmentEntry, Ledger, LedgerEntry, LiabilityEntry}
import entystal.model._
import entystal.util.{CsvExporter, PdfExporter}
import entystal.viewmodel.RegistroData
import zio.{Runtime, UIO}

/** Servicio que centraliza las operaciones de registro y exportación del ledger.
  */
class RegistroService(private val ledger: Ledger)(implicit runtime: Runtime[Any]) {

  def registrarActivo(asset: Asset): UIO[Unit] =
    ledger.recordAsset(asset)

  def registrarPasivo(liability: Liability): UIO[Unit] =
    ledger.recordLiability(liability)

  def registrarInversion(investment: Investment): UIO[Unit] =
    ledger.recordInvestment(investment)

  /** Registra un elemento en función de su tipo.
    */
  def registrar(data: RegistroData): Unit = {
    val ts = System.currentTimeMillis()
    data.tipo match {
      case "activo" =>
        val asset = DataAsset(data.identificador, data.descripcion, ts, BigDecimal(1))
        zio.Unsafe.unsafe { implicit u => runtime.unsafe.run(registrarActivo(asset)).getOrThrow() }
      case "pasivo" =>
        val liab = EthicalLiability(data.identificador, data.descripcion, ts, BigDecimal(1))
        zio.Unsafe.unsafe { implicit u => runtime.unsafe.run(registrarPasivo(liab)).getOrThrow() }
      case _        =>
        val qty = BigDecimal(data.descripcion)
        val inv = BasicInvestment(data.identificador, qty, ts)
        zio.Unsafe.unsafe { implicit u => runtime.unsafe.run(registrarInversion(inv)).getOrThrow() }
    }
  }

  /** Obtiene todo el historial almacenado. */
  def history: List[LedgerEntry] =
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(ledger.getHistory).getOrThrow()
    }

  /** Calcula los totales por tipo de registro. */
  def aggregateTotals(): UIO[(BigDecimal, BigDecimal, BigDecimal)] =
    ledger.getHistory.map { entries =>
      entries.foldLeft((BigDecimal(0), BigDecimal(0), BigDecimal(0))) {
        case ((a, l, i), AssetEntry(asset))    => (a + asset.value, l, i)
        case ((a, l, i), LiabilityEntry(liab)) => (a, l + liab.amount, i)
        case ((a, l, i), InvestmentEntry(inv)) => (a, l, i + inv.quantity)
      }
    }

  /** Exporta el historial completo a CSV. */
  def exportCsv(path: String): String = {
    val entries = history
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(CsvExporter.save(entries, path)).getOrThrow()
    }
    s"CSV exportado en $path"
  }

  /** Exporta el historial completo a PDF. */
  def exportPdf(path: String): String = {
    val entries = history
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(PdfExporter.save(entries, path)).getOrThrow()
    }
    s"PDF exportado en $path"
  }
}
