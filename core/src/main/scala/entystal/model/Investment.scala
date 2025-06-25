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
) extends Investment