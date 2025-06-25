package entystal.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AssetSpec extends AnyFlatSpec with Matchers {
  "Un DataAsset" should "mantener su valor" in {
    val asset = DataAsset("a1", "info", 1L, 100)
    asset.value shouldBe 100
  }
}
