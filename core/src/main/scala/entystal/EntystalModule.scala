package entystal

import entystal.ledger.Ledger
import entystal.model.{Asset, Balance, Liability}

object EntystalModule {
  def empty: Ledger = Ledger(Balance(Nil, Nil))

  def demoLedger: Ledger =
    empty
      .addAsset(Asset("cash", 1000))
      .addLiability(Liability("debt", 200))
}
