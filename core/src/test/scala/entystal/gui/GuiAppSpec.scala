package entystal.gui

import javafx.stage.Stage
import javafx.scene.control.{Button, Label}
import org.junit.Test
import org.testfx.framework.junit.ApplicationTest
import org.scalatest.matchers.should.Matchers
import entystal.ledger.AssetEntry
import entystal.model.DataAsset
import zio.Runtime

/** Pruebas de interfaz con TestFX */
class GuiAppSpec extends ApplicationTest with Matchers {
  private implicit val runtime: Runtime[Any] = Runtime.default

  override def start(stage: Stage): Unit = {
    new GuiAppTestApp().start(stage)
  }

  @Test def botonDeshabilitadoAlInicio(): Unit = {
    val btn = lookup("#registrarBtn").queryAs(classOf[Button])
    btn.isDisable shouldBe true
  }

  @Test def registrarActivo(): Unit = {
    clickOn("#idField").write("a1")
    clickOn("#descField").write("dato gui")
    clickOn("#registrarBtn")

    val label = lookup("#mensajeLabel").queryAs(classOf[Label])
    label.getText shouldBe "Registro completado"

    val history = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(GuiApp.appLedger.getHistory).getOrThrow()
    }
    history.exists {
      case AssetEntry(a: DataAsset) => a.id == "a1" && a.data == "dato gui"
      case _                        => false
    } shouldBe true
  }
}
