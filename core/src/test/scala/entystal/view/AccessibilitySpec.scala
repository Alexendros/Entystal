package entystal.view

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scalafx.application.Platform
import entystal.service.{RegistroService, TestNotifier}
import entystal.viewmodel.RegistroViewModel
import entystal.ledger._
import entystal.model._
import zio.Runtime

class AccessibilitySpec extends AnyFlatSpec with Matchers {
  "Vistas" should "definir texto accesible y mnemonicos" in {
    try Platform.startup(() => {})
    catch { case _: Exception => cancel("JavaFX no disponible") }
    implicit val rt: Runtime[Any] = Runtime.default
    val ledger: Ledger            = zio.Unsafe.unsafe { implicit u =>
      rt.unsafe.run(zio.ZIO.scoped(InMemoryLedger.live.build.map(_.get))).getOrThrow()
    }
    val service                   = new RegistroService(ledger)
    val notifier                  = new TestNotifier
    val vm                        = new RegistroViewModel(service, notifier)

    val mainView = new MainView(vm, ledger)
    mainView.registrarBtn.accessibleText.value shouldBe "Registrar"
    mainView.registrarBtn.mnemonicParsing.value shouldBe true

    val busquedaView = new BusquedaView(ledger)
    busquedaView.buscarBtn.accessibleText.value shouldBe "Buscar"
    busquedaView.buscarBtn.mnemonicParsing.value shouldBe true

    val entry       = AssetEntry(DataAsset("a1", "d", 1L, BigDecimal(1)))
    val edicionView = new EdicionView(entry)
    edicionView.guardarBtn.accessibleText.value shouldBe "Guardar cambios"
    edicionView.guardarBtn.mnemonicParsing.value shouldBe true
  }
}
