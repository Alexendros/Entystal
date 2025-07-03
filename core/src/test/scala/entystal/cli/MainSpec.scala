import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import entystal.cli.{Main, Config}
import scopt.OParser
import entystal.model._

class MainSpec extends AnyFlatSpec with Matchers {
  private def parse(args: Array[String]): Config =
    OParser.parse(Main.parser, args, Config()).get

  "La CLI" should "construir un DataAsset con argumentos validos" in {
    val cfg   = parse(Array("--mode", "asset", "--assetId", "a1", "--assetDesc", "dato"))
    val ts    = 1L
    val asset = DataAsset(cfg.assetId.get, cfg.assetDesc.get, ts, BigDecimal(1))
    asset.id shouldBe "a1"
    asset.data shouldBe "dato"
    asset.value shouldBe BigDecimal(1)
  }

  it should "construir un BasicLiability cambiando el modo" in {
    val cfg       = parse(Array("--mode", "liability", "--assetId", "l1", "--assetDesc", "pago"))
    val ts        = 2L
    val liability = BasicLiability(cfg.assetId.get, BigDecimal(1), ts)
    liability.id shouldBe "l1"
    liability.amount shouldBe BigDecimal(1)
  }

  it should "construir un BasicInvestment cambiando el modo" in {
    val cfg        = parse(Array("--mode", "investment", "--assetId", "i1", "--assetDesc", "inv"))
    val ts         = 3L
    val investment = BasicInvestment(cfg.assetId.get, BigDecimal(1), ts)
    investment.id shouldBe "i1"
    investment.quantity shouldBe BigDecimal(1)
  }

  it should "aceptar descripciones con espacios" in {
    val cfg =
      parse(Array("--mode", "asset", "--assetId", "a2", "--assetDesc", "descripcion con espacios"))
    cfg.assetDesc shouldBe Some("descripcion con espacios")
  }
}
