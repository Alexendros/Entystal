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

final case class EthicalLiability(
    id: String,
    description: String,
    timestamp: Long,
    amount: BigDecimal
) extends Liability

final case class StrategicLiability(
    id: String,
    reason: String,
    timestamp: Long,
    amount: BigDecimal
) extends Liability

final case class LegalLiability(
    id: String,
    law: String,
    timestamp: Long,
    amount: BigDecimal
) extends Liability
