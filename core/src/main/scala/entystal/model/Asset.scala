package entystal.model

trait Asset {
  def id: String
  def timestamp: Long
  def value: BigDecimal
}

final case class DataAsset(
    id: String,
    data: String,
    timestamp: Long,
    value: BigDecimal

final case class CodeAsset(
    id: String,
    repo: String,
    timestamp: Long,
    value: BigDecimal
) extends Asset

final case class ReputationAsset(
    id: String,
    score: Int,
    timestamp: Long,
    value: BigDecimal
) extends Asset
) extends Asset