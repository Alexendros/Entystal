package entystal.service
import entystal.ledger.Ledger
import entystal.model.{Asset, Investment, Liability}
import zio.UIO

/** Servicio que abstrae el registro de eventos delegando en el Ledger */
class RegistroService(private val ledger: Ledger) {
  def registrarActivo(asset: Asset): UIO[Unit] =
    ledger.recordAsset(asset)

  def registrarPasivo(liability: Liability): UIO[Unit] =
    ledger.recordLiability(liability)

  def registrarInversion(investment: Investment): UIO[Unit] =
    ledger.recordInvestment(investment)
}
