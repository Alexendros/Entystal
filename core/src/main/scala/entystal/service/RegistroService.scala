package entystal.service

import entystal.ledger.{Ledger, AssetEntry, LiabilityEntry, InvestmentEntry}
import zio.UIO

/** Servicio para obtener datos agregados del registro */
class RegistroService(ledger: Ledger) {
  /**
    * Calcula los totales en valor (activos), monto (pasivos) y cantidad (inversiones).
    * @return tupla con (totalActivos, totalPasivos, totalInversiones)
    */
  def aggregateTotals(): UIO[(BigDecimal, BigDecimal, BigDecimal)] =
    ledger.getHistory.map { entries =>
      val (a, l, i) = entries.foldLeft((BigDecimal(0), BigDecimal(0), BigDecimal(0))) {
        case ((accA, accL, accI), AssetEntry(asset))       => (accA + asset.value, accL, accI)
        case ((accA, accL, accI), LiabilityEntry(liab))    => (accA, accL + liab.amount, accI)
        case ((accA, accL, accI), InvestmentEntry(inv))    => (accA, accL, accI + inv.quantity)
      }
      (a, l, i)
    }
}
