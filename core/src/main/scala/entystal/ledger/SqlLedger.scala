package entystal.ledger

import entystal.model._
import zio._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
import zio.interop.catz._

/** Ledger persistente sobre PostgreSQL usando Doobie */
class SqlLedger(xa: Transactor[Task]) extends Ledger {

  /** Inserta un activo genérico en la tabla 'asset'. */
  override def recordAsset(asset: Asset): UIO[Unit] = {
    val desc = asset match {
      case DataAsset(_, data, _, _)        => data
      case CodeAsset(_, repo, _, _)        => repo
      case ReputationAsset(_, score, _, _) => s"score: $score"
    }
    sql"INSERT INTO asset (id, description, timestamp) VALUES (${asset.id}, $desc, ${asset.timestamp})".update.run
      .transact(xa)
      .orDie
      .unit
  }

  /** Inserta un pasivo genérico en la tabla 'liability'. */
  override def recordLiability(liability: Liability): UIO[Unit] = {
    val desc = liability match {
      case BasicLiability(_, _, _)                => "basic"
      case EthicalLiability(_, description, _, _) => description
      case StrategicLiability(_, reason, _, _)    => reason
      case LegalLiability(_, law, _, _)           => law
    }
    sql"INSERT INTO liability (id, description, timestamp) VALUES (${liability.id}, $desc, ${liability.timestamp})".update.run
      .transact(xa)
      .orDie
      .unit
  }

  /** Inserta una inversión genérica en la tabla 'investment'. */
  override def recordInvestment(investment: Investment): UIO[Unit] = {
    val desc = investment match {
      case BasicInvestment(_, quantity, _)       => quantity.toString
      case EconomicInvestment(_, quantity, _)    => quantity.toString
      case HumanInvestment(_, quantity, _)       => quantity.toString
      case OperationalInvestment(_, quantity, _) => quantity.toString
    }
    sql"INSERT INTO investment (id, description, timestamp) VALUES (${investment.id}, $desc, ${investment.timestamp})".update.run
      .transact(xa)
      .orDie
      .unit
  }

  /** Recupera el historial completo de eventos de las tres tablas. */
  override def getHistory: UIO[List[LedgerEntry]] = {
    val assetsIO      =
      sql"SELECT id, description, timestamp FROM asset"
        .query[(String, String, Long)]
        .to[List]
        .map(_.map { case (id, desc, ts) =>
          AssetEntry(DataAsset(id, desc, ts, BigDecimal(1)))
        })
    val liabilitiesIO =
      sql"SELECT id, description, timestamp FROM liability"
        .query[(String, String, Long)]
        .to[List]
        .map(_.map { case (id, desc, ts) =>
          LiabilityEntry(EthicalLiability(id, desc, ts, BigDecimal(1)))
        })
    val investmentsIO =
      sql"SELECT id, description, timestamp FROM investment"
        .query[(String, String, Long)]
        .to[List]
        .map(_.map { case (id, desc, ts) =>
          InvestmentEntry(EconomicInvestment(id, BigDecimal(1), ts))
        })

    val combined: ConnectionIO[List[LedgerEntry]] = for {
      a <- assetsIO
      l <- liabilitiesIO
      i <- investmentsIO
    } yield a ++ l ++ i

    combined.transact(xa).orDie
  }
}

object SqlLedger {
  def layer(xa: Transactor[Task]): ULayer[Ledger] =
    ZLayer.succeed(new SqlLedger(xa))
}
