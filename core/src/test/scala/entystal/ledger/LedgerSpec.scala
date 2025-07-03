package entystal.ledger

import entystal.model.DataAsset
import zio.test.{ZIOSpecDefault, assertTrue, Spec, TestEnvironment}
import zio.test._
import zio.Scope

object LedgerSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("LedgerSpec")(
      test("Registra y recupera activos correctamente") {
        val asset = DataAsset("id-1", "Datos relevantes", 1L, BigDecimal(42))
        for {
          ledger  <- InMemoryLedger.live.build.map(_.get)
          _       <- ledger.recordAsset(asset)
          history <- ledger.getHistory
        } yield assertTrue(history.exists(_ == AssetEntry(asset)))
      }
    )
}
