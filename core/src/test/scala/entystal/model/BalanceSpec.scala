import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import entystal.model._

class BalanceSpec extends AnyFlatSpec with Matchers {
  "netWorth" should "calcularse correctamente" in {
    val assets      = List(
      DataAsset("a1", "d1", 1L, BigDecimal(10)),
      DataAsset("a2", "d2", 2L, BigDecimal(5))
    )
    val liabilities = List(
      BasicLiability("l1", BigDecimal(4), 3L)
    )
    val balance     = Balance(assets, liabilities)
    balance.netWorth shouldBe BigDecimal(11)
  }

  it should "soportar listas vacías" in {
    Balance(Nil, Nil).netWorth shouldBe BigDecimal(0)
  }
}
