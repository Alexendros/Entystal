package entystal.ledger

import entystal.model._
import zio._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor

/** Ledger persistente sobre PostgreSQL usando Doobie */
class SqlLedger(xa: Transactor[Task]) extends Ledger {
  /** Inserta un activo genérico en la tabla 'asset'. */
  override def recordAsset(asset: Asset): UIO[Unit] =
    sql"INSERT INTO asset (id, description, timestamp) VALUES (${asset.id}, ${asset.toString}, ${asset.timestamp})"
      .update
      .run
      .transact(xa)
      .orDie
      .unit

  /** Inserta un pasivo genérico en la tabla 'liability'. */
  override def recordLiability(liability: Liability): UIO[Unit] =
    sql"INSERT INTO liability (id, description, timestamp) VALUES (${liability.id}, ${liability.toString}, ${liability.timestamp})"
      .update
      .run
      .transact(xa)
      .orDie
      .unit

  /** Inserta una inversión genérica en la tabla 'investment'. */
  override def recordInvestment(investment: Investment): UIO[Unit] =
    sql"INSERT INTO investment (id, description, timestamp) VALUES (${investment.id}, ${investment.toString}, ${investment.timestamp})"
      .update
      .run
      .transact(xa)
      .orDie
      .unit

  /** Recupera el historial completo de eventos de las tres tablas. */
  override def getHistory: UIO[List[LedgerEntry]] = {
    val assets =
      sql"SELECT id, description, timestamp FROM asset".query[(String, String, Long)].to[List].map(_.map {
        case (id, desc, ts) => AssetEntry(DataAsset(id, desc, ts, BigDecimal(1)))
      })
    val liabilities =
      sql"SELECT id, description, timestamp FROM liability".query[(String, String, Long)].to[List].map(_.map {
        case (id, desc, ts) => LiabilityEntry(EthicalLiability(id, desc, ts, BigDecimal(1)))
      })
    val investments =
      sql"SELECT id, description, timestamp FROM investment".query[(String, String, Long)].to[List].map(_.map {
        case (id, desc, ts) => InvestmentEntry(EconomicInvestment(id, BigDecimal(1), ts))
      })

    (assets ++ liabilities ++ investments).transact(xa).orDie.map(_.flatten)
  }
}

object SqlLedger {
  def layer(xa: Transactor[Task]): ULayer[Ledger] =
    ZLayer.succeed(new SqlLedger(xa))
}
