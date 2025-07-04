package entystal.service

import zio.test.{ZIOSpecDefault, Spec, TestEnvironment, assertTrue}
import zio.Scope
import entystal.model._
import entystal.ledger.{InMemoryLedger, AssetEntry, LiabilityEntry, InvestmentEntry}

object RegistroServiceSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("RegistroServiceSpec")(
      test("registra un activo delegando en el Ledger") {
        val asset = DataAsset("a1", "dato", 1L, BigDecimal(1))
        for {
          ledger  <- InMemoryLedger.live.build.map(_.get)
          service  = new RegistroService(ledger)
          _       <- service.registrarActivo(asset)
          history <- ledger.getHistory
        } yield assertTrue(history.exists(_ == AssetEntry(asset)))
      },
      test("registra un pasivo delegando en el Ledger") {
        val liability = EthicalLiability("p1", "desc", 2L, BigDecimal(1))
        for {
          ledger  <- InMemoryLedger.live.build.map(_.get)
          service  = new RegistroService(ledger)
          _       <- service.registrarPasivo(liability)
          history <- ledger.getHistory
        } yield assertTrue(history.exists(_ == LiabilityEntry(liability)))
      },
      test("registra una inversion delegando en el Ledger") {
        val investment = BasicInvestment("i1", BigDecimal(2), 3L)
        for {
          ledger  <- InMemoryLedger.live.build.map(_.get)
          service  = new RegistroService(ledger)
          _       <- service.registrarInversion(investment)
          history <- ledger.getHistory
        } yield assertTrue(history.exists(_ == InvestmentEntry(investment)))
      }
    )
}
