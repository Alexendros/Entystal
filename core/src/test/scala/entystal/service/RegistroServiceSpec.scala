package entystal.service

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import entystal.ledger.{InMemoryLedger, Ledger, AssetEntry, LiabilityEntry, InvestmentEntry}
import entystal.model._
import entystal.viewmodel.RegistroData

class RegistroServiceSpec extends AnyFlatSpec with Matchers {
  "aggregateTotals" should "sumar correctamente" in {
    implicit val runtime: zio.Runtime[Any] = zio.Runtime.default
    val ledger                             = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe
        .run(zio.ZIO.scoped(InMemoryLedger.live.build.map(_.get)))
        .getOrThrow()
    }
    val service                            = new RegistroService(ledger)
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe
        .run {
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
        }
        .getOrThrow()
    }
  }

  "registrar" should "crear modelos segun tipo" in {
    val runtime = zio.Runtime.default
    val ledger  = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe
        .run(zio.ZIO.scoped(InMemoryLedger.live.build.map(_.get)))
        .getOrThrow()
    }
    val service = new RegistroService(ledger)
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe
        .run {
          for {
            _ <- service.registrar(RegistroData("activo", "a1", "desc"))
            _ <- service.registrar(RegistroData("pasivo", "p1", ""))
            _ <- service.registrar(RegistroData("inversion", "i1", "5"))
            h <- ledger.getHistory
          } yield {
            h.collect { case AssetEntry(a) => a.id }.head shouldBe "a1"
            h.collect { case LiabilityEntry(l) => l.id }.head shouldBe "p1"
            h.collect { case InvestmentEntry(i) => i.id }.head shouldBe "i1"
          }
        }
        .getOrThrow()
    }
  }

  "exportCsv" should "generar archivos" in {
    val runtime = zio.Runtime.default
    val ledger  = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe
        .run(zio.ZIO.scoped(InMemoryLedger.live.build.map(_.get)))
        .getOrThrow()
    }
    val service = new RegistroService(ledger)
    val tmpCsv  = java.nio.file.Files.createTempFile("reg", ".csv")
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(service.registrar(RegistroData("activo", "a1", "d"))).getOrThrow()
    }
    service.exportCsv(tmpCsv.toString)
    assert(tmpCsv.toFile.exists())
    tmpCsv.toFile.delete()
  }

  "exportPdf" should "generar archivos" in {
    val runtime = zio.Runtime.default
    val ledger  = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe
        .run(zio.ZIO.scoped(InMemoryLedger.live.build.map(_.get)))
        .getOrThrow()
    }
    val service = new RegistroService(ledger)
    val tmpPdf  = java.nio.file.Files.createTempFile("reg", ".pdf")
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(service.registrar(RegistroData("pasivo", "p1", ""))).getOrThrow()
    }
    service.exportPdf(tmpPdf.toString)
    assert(tmpPdf.toFile.exists())
    tmpPdf.toFile.delete()
  }
}
