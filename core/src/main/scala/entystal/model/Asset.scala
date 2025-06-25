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
) extends Asset