package entystal.ledger

import entystal.model.{Asset, Balance, Liability}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class LedgerSpec extends AnyFlatSpec with Matchers {
  "A Ledger" should "update balance" in {
    val ledger = Ledger(Balance(Nil, Nil))
      .addAsset(Asset("cash", 100))
      .addLiability(Liability("loan", 50))

    ledger.balance.netWorth shouldBe 50
  }
}
