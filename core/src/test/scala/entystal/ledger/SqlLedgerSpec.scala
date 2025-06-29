package entystal.ledger

import zio._
import zio.test._
import zio.test.Assertion._
import doobie.util.transactor.Transactor
import entystal.model._

/** Pruebas de integración del SqlLedger usando PostgreSQL */
object SqlLedgerSpec extends ZIOSpecDefault {
  private val transactorLayer = ZLayer.scoped {
    ZIO.attempt {
      Transactor.fromDriverManager[Task](
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost:5432/entystal",
        "postgres",
        "password"
      )
    }
  }

  override def spec = suite("SqlLedgerSpec")(
    test("Registra y recupera un activo persistente") {
      val asset = DataAsset("id-psql-1", "Activo PSQL", 123L, BigDecimal(99))
      for {
        xa     <- ZIO.service[Transactor[Task]]
        ledger  = new SqlLedger(xa)
        _      <- ledger.recordAsset(asset)
        // TODO: implementar getHistory y verificar contenido
      } yield assertCompletes
    }
  ).provideLayerShared(transactorLayer)
}
