package entystal.view

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scalafx.application.Platform
import entystal.ledger._
import entystal.model._
import zio.Runtime

class BusquedaViewSpec extends AnyFlatSpec with Matchers {
  "BusquedaView" should "refrescar la tabla tras eliminar" in {
    try Platform.startup(() => {})
    catch { case _: Exception => cancel("JavaFX no disponible") }
    implicit val rt: Runtime[Any] = Runtime.default
    val ledger: Ledger            = zio.Unsafe.unsafe { implicit u =>
      rt.unsafe.run(zio.ZIO.scoped(InMemoryLedger.live.build.map(_.get))).getOrThrow()
    }
    zio.Unsafe.unsafe { implicit u =>
      rt.unsafe.run(ledger.recordAsset(DataAsset("a1", "d", 1L, BigDecimal(1)))).getOrThrow()
    }
    val view                      = new BusquedaView(ledger)
    view.cargar()
    view.tabla.items().size shouldBe 1
    zio.Unsafe.unsafe { implicit u => rt.unsafe.run(ledger.deleteEntry("a1")).getOrThrow() }
    view.cargar()
    view.tabla.items().size shouldBe 0
  }
}
