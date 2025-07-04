package entystal.gui

import scalafx.application.JFXApp3
import scalafx.stage.Stage
import entystal.{EntystalModule}
import entystal.ledger.Ledger
import entystal.viewmodel.{RegistroValidator, RegistroViewModel}
import entystal.service.{RegistroService, DialogNotifier}
import entystal.view.MainView
import entystal.gui.ThemeManager
import entystal.i18n.I18n
import zio.Runtime

/** Lanzador principal de la interfaz gráfica */
object GuiApp extends JFXApp3 {
  override def start(): Unit = {
    implicit val runtime: Runtime[Any] = Runtime.default
    val ledger: Ledger = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe
        .run(zio.ZIO.scoped(EntystalModule.layer.build.map(_.get)))
        .getOrThrow()
    }
    val service                        = new RegistroService(ledger)
    val validator                      = new RegistroValidator
    val vm                             = new RegistroViewModel(service, validator, DialogNotifier)
    val view                           = new MainView(vm, ledger)

    stage = new JFXApp3.PrimaryStage {
      title = I18n("app.title")
      scene = view.scene
    }
    // Aplicar tema guardado al iniciar
    ThemeManager.applyTheme(view.scene, ThemeManager.loadTheme())
  }
}
