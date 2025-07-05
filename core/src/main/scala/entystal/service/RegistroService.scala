package entystal.service

import entystal.ledger._
import entystal.model._
import entystal.viewmodel.RegistroData
import entystal.util.{CsvExporter, PdfExporter, PrivacyUtils}
import zio.{Runtime, UIO}

/** Servicio que registra y consulta el ledger */
class RegistroService(private val ledger: Ledger) {
  private val runtime                          = Runtime.default
  def registrarActivo(asset: Asset): UIO[Unit] = asset match {
    case d: DataAsset =>
      ledger.recordAsset(d.copy(data = PrivacyUtils.sanitize(d.data)))
    case other        =>
      ledger.recordAsset(other)
  }

  def registrarPasivo(liability: Liability): UIO[Unit] = liability match {
    case e: EthicalLiability =>
      ledger.recordLiability(e.copy(description = PrivacyUtils.sanitize(e.description)))
    case s: StrategicLiability =>
      ledger.recordLiability(s.copy(reason = PrivacyUtils.sanitize(s.reason)))
    case l: LegalLiability =>
      ledger.recordLiability(l.copy(law = PrivacyUtils.sanitize(l.law)))
    case other =>
      ledger.recordLiability(other)
  }

  def registrarInversion(investment: Investment): UIO[Unit] =
    ledger.recordInvestment(investment)

  /** Crea el modelo correspondiente a partir de los datos y lo registra */
  def registrar(data: RegistroData): UIO[Unit] =
    data.tipo match {
      case "activo"    =>
        registrarActivo(
          DataAsset(
            data.identificador,
            PrivacyUtils.sanitize(data.descripcion),
            System.currentTimeMillis(),
            BigDecimal(1)
          )
        )
      case "pasivo"    =>
        registrarPasivo(
          BasicLiability(data.identificador, BigDecimal(1), System.currentTimeMillis())
        )
      case "inversion" =>
        registrarInversion(
          BasicInvestment(
            data.identificador,
            BigDecimal(data.descripcion),
            System.currentTimeMillis()
          )
        )
      case _           => zio.ZIO.unit
    }

  def actualizar(id: String, valor: String): UIO[Unit] =
    ledger.updateEntry(id, valor)

  def eliminar(id: String): UIO[Unit] =
    ledger.deleteEntry(id)

  /** Suma los totales de activos, pasivos e inversiones */
  def aggregateTotals(): UIO[(BigDecimal, BigDecimal, BigDecimal)] =
    ledger.getHistory.map { history =>
      history.foldLeft((BigDecimal(0), BigDecimal(0), BigDecimal(0))) {
        case ((a, l, i), AssetEntry(asset))    => (a + asset.value, l, i)
        case ((a, l, i), LiabilityEntry(liab)) => (a, l + liab.amount, i)
        case ((a, l, i), InvestmentEntry(inv)) => (a, l, i + inv.quantity)
      }
    }

  /** Devuelve el historial completo de eventos */
  def obtenerHistorial: UIO[List[LedgerEntry]] =
    ledger.getHistory

  /** Exporta el historial completo a CSV y devuelve un mensaje de confirmación */
  def exportCsv(path: String): String = {
    val entries = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(ledger.getHistory).getOrThrow()
    }
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(CsvExporter.save(entries, path)).getOrThrow()
    }
    s"Historial CSV exportado a $path"
  }

  /** Exporta el historial completo a PDF y devuelve un mensaje de confirmación */
  def exportPdf(path: String): String = {
    val entries = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(ledger.getHistory).getOrThrow()
    }
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(PdfExporter.save(entries, path)).getOrThrow()
    }
    s"Historial PDF exportado a $path"
  }
}
