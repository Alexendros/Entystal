package entystal.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AssetSpec extends AnyFlatSpec with Matchers {
  "An Asset" should "hold its value" in {
    val asset = Asset("a1", 100)
    asset.value shouldBe 100
  }
}
