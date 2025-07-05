package entystal.ledger

import zio._
import zio.test._
import zio.test.Assertion._
import zio.interop.catz._
import doobie.util.transactor.Transactor
import entystal.model._

/** Pruebas de integración del SqlLedger usando PostgreSQL */
object SqlLedgerSpec extends ZIOSpecDefault {
  private val dbUserOpt = sys.env.get("PGUSER")
  private val dbPassOpt = sys.env.get("PGPASSWORD")

  private val dbUrl = "jdbc:postgresql://localhost:5432/entystal"

  /** Intento de conexión para saber si PostgreSQL está disponible */
  private val connectionCheck: Task[Boolean] =
    (dbUserOpt, dbPassOpt) match {
      case (Some(user), Some(pass)) =>
        ZIO
          .attemptBlocking {
            val conn = java.sql.DriverManager.getConnection(dbUrl, user, pass)
            try {
              val st = conn.createStatement()
              st.execute("SELECT 1")
              true
            } finally conn.close()
          }
          .tapError(e => ZIO.logError(s"No se pudo conectar a PostgreSQL: ${e.getMessage}"))
          .orElseSucceed(false)
      case _                        =>
        ZIO.logWarning("Credenciales de PostgreSQL no definidas") *> ZIO.succeed(false)
    }

  private val transactorLayer = ZLayer.scoped {
    (dbUserOpt, dbPassOpt) match {
      case (Some(user), Some(pass)) =>
        ZIO.attempt {
          val props = new java.util.Properties()
          props.setProperty("user", user)
          props.setProperty("password", pass)
          Transactor.fromDriverManager[Task](
            "org.postgresql.Driver",
            dbUrl,
            props,
            None
          )
        }
      case _                        =>
        ZIO.fail(new Exception("Credenciales de PostgreSQL no definidas"))
    }
  }

  override def spec = suite("SqlLedgerSpec")(
    test("Registra y recupera un activo persistente") {
      val asset = DataAsset("id-psql-1", "Activo PSQL", 123L, BigDecimal(99))
      for {
        xa      <- ZIO.service[Transactor[Task]]
        ledger   = new SqlLedger(xa)
        _       <- ledger.recordAsset(asset)
        history <- ledger.getHistory
      } yield assertTrue(history.exists {
        case AssetEntry(a) =>
          a.id == asset.id && a
            .isInstanceOf[DataAsset] && a.asInstanceOf[DataAsset].data == asset.data
        case _             => false
      })
    }
  ).provideLayerShared(transactorLayer).whenZIO(connectionCheck)
}
