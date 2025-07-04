package entystal.gui

import scalafx.application.JFXApp3
import scalafx.stage.Stage
import entystal.{EntystalModule}
import entystal.ledger.Ledger
import entystal.viewmodel.RegistroViewModel
import entystal.view.MainView
import entystal.gui.ThemeManager
import zio.Runtime

/** Lanzador principal de la interfaz gráfica */
object GuiApp extends JFXApp3 {
  override def start(): Unit = {
    implicit val runtime: Runtime[Any] = Runtime.default
    val ledger: Ledger                 = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe
        .run(zio.ZIO.scoped(EntystalModule.layer.build.map(_.get)))
        .getOrThrow()
    }
    val vm                             = new RegistroViewModel(ledger)
    val view                           = new MainView(vm)

    stage = new JFXApp3.PrimaryStage {
      title = "Entystal GUI"
      scene = view.scene
    }
    // Aplicar tema guardado al iniciar
    ThemeManager.applyTheme(view.scene, ThemeManager.loadTheme())
  }
}
