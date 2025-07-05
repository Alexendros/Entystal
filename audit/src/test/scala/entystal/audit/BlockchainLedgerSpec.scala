package entystal.audit

import entystal.model.DataAsset
import entystal.ledger.AssetEntry
import zio.test.{ZIOSpecDefault, assertTrue, Spec, TestEnvironment}
import zio.Scope

object BlockchainLedgerSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("BlockchainLedgerSpec")(
      test("escribe y lee bloques") {
        val asset = DataAsset("b1", "dato", 1L, BigDecimal(5))
        for {
          ledger <- InMemoryBlockchainLedger.layer().build.map(_.get)
          _      <- ledger.recordAsset(asset)
          blocks <- ledger.getBlocks
        } yield assertTrue(blocks.exists(_.entry == AssetEntry(asset)))
      }
    )
}
