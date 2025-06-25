package entystal.model

import zio.json._

/** Codificadores JSON para los modelos */
object JsonCodecs {
  implicit val dataAssetCodec: JsonCodec[DataAsset] = DeriveJsonCodec.gen[DataAsset]
  implicit val codeAssetCodec: JsonCodec[CodeAsset] = DeriveJsonCodec.gen[CodeAsset]
  implicit val reputationAssetCodec: JsonCodec[ReputationAsset] = DeriveJsonCodec.gen[ReputationAsset]

  implicit val ethicalLiabilityCodec: JsonCodec[EthicalLiability] = DeriveJsonCodec.gen[EthicalLiability]
  implicit val strategicLiabilityCodec: JsonCodec[StrategicLiability] = DeriveJsonCodec.gen[StrategicLiability]
  implicit val legalLiabilityCodec: JsonCodec[LegalLiability] = DeriveJsonCodec.gen[LegalLiability]

  implicit val economicInvestmentCodec: JsonCodec[EconomicInvestment] = DeriveJsonCodec.gen[EconomicInvestment]
  implicit val humanInvestmentCodec: JsonCodec[HumanInvestment] = DeriveJsonCodec.gen[HumanInvestment]
  implicit val operationalInvestmentCodec: JsonCodec[OperationalInvestment] = DeriveJsonCodec.gen[OperationalInvestment]
}
