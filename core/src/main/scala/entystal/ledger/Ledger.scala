package entystal.ledger

import entystal.model.{Asset, Investment, Liability}
import zio.{Ref, UIO, ULayer, ZLayer}

/** Registro funcional en memoria de eventos contables */
trait Ledger {
  def recordAsset(asset: Asset): UIO[Unit]
  def recordLiability(liability: Liability): UIO[Unit]
  def recordInvestment(investment: Investment): UIO[Unit]
  def getHistory: UIO[List[LedgerEntry]]
}

sealed trait LedgerEntry {
  def id: String
  def timestamp: Long
}

final case class AssetEntry(asset: Asset) extends LedgerEntry {
  val id: String      = asset.id
  val timestamp: Long = asset.timestamp
}

final case class LiabilityEntry(liability: Liability) extends LedgerEntry {
  val id: String      = liability.id
  val timestamp: Long = liability.timestamp
}

final case class InvestmentEntry(investment: Investment) extends LedgerEntry {
  val id: String      = investment.id
  val timestamp: Long = investment.timestamp
}

object InMemoryLedger {
  def live: ULayer[Ledger] =
    ZLayer {
      for {
        ref <- Ref.make(List.empty[LedgerEntry])
      } yield new Ledger {
        override def recordAsset(asset: Asset): UIO[Unit]                =
          ref.update(_ :+ AssetEntry(asset))
        override def recordLiability(liability: Liability): UIO[Unit]    =
          ref.update(_ :+ LiabilityEntry(liability))
        override def recordInvestment(investment: Investment): UIO[Unit] =
          ref.update(_ :+ InvestmentEntry(investment))
        override def getHistory: UIO[List[LedgerEntry]]                  = ref.get
      }
    }
}
