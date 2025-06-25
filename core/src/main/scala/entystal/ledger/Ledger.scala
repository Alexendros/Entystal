package entystal.ledger

import entystal.model.{Asset, Balance, Liability}

final case class Ledger(balance: Balance) {
  def addAsset(asset: Asset): Ledger =
    copy(balance = balance.copy(assets = asset :: balance.assets))

  def addLiability(liability: Liability): Ledger =
    copy(balance = balance.copy(liabilities = liability :: balance.liabilities))
}
