package entystal.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AssetSpec extends AnyFlatSpec with Matchers {
  "Un DataAsset" should "mantener su valor" in {
    val asset = DataAsset("a1", "info", 1L, BigDecimal(100))
    asset.value shouldBe BigDecimal(100)
  }
}