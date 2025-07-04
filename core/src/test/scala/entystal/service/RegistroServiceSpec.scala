package entystal.service

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import entystal.ledger.{InMemoryLedger, Ledger}
import entystal.model._

class RegistroServiceSpec extends AnyFlatSpec with Matchers {
  "aggregateTotals" should "sumar correctamente" in {
    val runtime = zio.Runtime.default
    val ledger  = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe
        .run(zio.ZIO.scoped(InMemoryLedger.live.build.map(_.get)))
        .getOrThrow()
    }
    val service = new RegistroService(ledger)
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run {
        for {
          _ <- ledger.recordAsset(DataAsset("a1", "d", 1L, BigDecimal(10)))
          _ <- ledger.recordLiability(BasicLiability("l1", BigDecimal(5), 2L))
          _ <- ledger.recordInvestment(BasicInvestment("i1", BigDecimal(3), 3L))
          t <- service.aggregateTotals()
        } yield {
          t._1 shouldBe BigDecimal(10)
          t._2 shouldBe BigDecimal(5)
          t._3 shouldBe BigDecimal(3)
        }
      }.getOrThrow()
    }
  }
}
