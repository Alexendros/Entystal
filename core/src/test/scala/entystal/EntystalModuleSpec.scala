package entystal

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EntystalModuleSpec extends AnyFlatSpec with Matchers {
  "EntystalModule" should "provide a demo ledger" in {
    val ledger = EntystalModule.demoLedger
    ledger.balance.netWorth shouldBe 800
  }
}
