package entystal.model

final case class Balance(assets: List[Asset], liabilities: List[Liability]) {
  def netWorth: BigDecimal =
    assets.map(_.value).sum - liabilities.map(_.amount).sum
}
