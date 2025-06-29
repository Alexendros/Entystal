package entystal.ledger

import entystal.model._
import zio._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor

/** Ledger persistente sobre PostgreSQL usando Doobie */
class SqlLedger(xa: Transactor[Task]) extends Ledger {
  override def recordAsset(asset: Asset): UIO[Unit] =
    sql"INSERT INTO asset (id, description, timestamp) VALUES (${asset.id}, ${asset.toString}, ${asset.timestamp})".update.run.transact(xa).orDie.unit

  // Métodos para los demás registros
  override def recordLiability(liability: Liability): UIO[Unit] =
    ZIO.unit // TODO implementar

  override def recordInvestment(investment: Investment): UIO[Unit] =
    ZIO.unit // TODO implementar

  override def getHistory: UIO[List[LedgerEntry]] =
    ZIO.succeed(List.empty) // TODO implementar
}

object SqlLedger {
  def layer(xa: Transactor[Task]): ULayer[Ledger] =
    ZLayer.succeed(new SqlLedger(xa))
}
