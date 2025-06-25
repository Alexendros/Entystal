package entystal.model

trait Investment {
  def id: String
  def timestamp: Long
  def quantity: BigDecimal
}

final case class BasicInvestment(
    id: String,
    quantity: BigDecimal,
    timestamp: Long

final case class EconomicInvestment(
    id: String,
    quantity: BigDecimal,
    timestamp: Long
) extends Investment

final case class HumanInvestment(
    id: String,
    quantity: BigDecimal,
    timestamp: Long
) extends Investment

final case class OperationalInvestment(
    id: String,
    quantity: BigDecimal,
    timestamp: Long
) extends Investment
) extends Investment