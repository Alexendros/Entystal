package entystal.model

trait Liability {
  def id: String
  def timestamp: Long
  def amount: BigDecimal
}

final case class BasicLiability(
    id: String,
    amount: BigDecimal,
    timestamp: Long
) extends Liability
