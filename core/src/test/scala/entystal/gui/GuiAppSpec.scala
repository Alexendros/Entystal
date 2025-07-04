package entystal.gui

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import javafx.embed.swing.JFXPanel
import scalafx.application.Platform
import scalafx.scene.control.{Button, TextField, Label}
import entystal.viewmodel.RegistroViewModel
import entystal.view.MainView
import entystal.ledger.{InMemoryLedger, AssetEntry}
import entystal.model.DataAsset
import zio.Runtime

/** Pruebas simplificadas de la GUI sin TestFX */
class GuiAppSpec extends AnyFlatSpec with Matchers {
  // Inicializar JavaFX Toolkit
  new JFXPanel()

  private implicit val runtime: Runtime[Any] = Runtime.default

  private def createView() = {
    val ledger = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe
        .run(zio.ZIO.scoped(InMemoryLedger.live.build.map(_.get)))
        .getOrThrow()
    }
    val vm   = new RegistroViewModel(ledger)
    val view = new MainView(vm)
    (view, ledger)
  }

  "La GUI" should "deshabilitar el botón al inicio" in {
    val (view, _) = createView()
    view.registrarBtn.disable.value shouldBe true
  }

  it should "registrar un activo" in {
    val (view, ledger) = createView()
    val idField   = view.idField
    val descField = view.descField
    val btn       = view.registrarBtn
    val msgLabel  = view.mensajeLabel

    Platform.runLater {
      idField.text = "a1"
      descField.text = "dato gui"
      btn.fire()
    }
    Thread.sleep(50) // esperar que JavaFX procese el evento

    msgLabel.text.value shouldBe "Registro completado"
    val history = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(ledger.getHistory).getOrThrow()
    }
    history.exists {
      case AssetEntry(a: DataAsset) => a.id == "a1" && a.data == "dato gui"
      case _                        => false
    } shouldBe true
  }
}
