package entystal.gui

import scalafx.application.JFXApp3
import scalafx.stage.Stage
import entystal.{EntystalModule}
import entystal.ledger.Ledger
import entystal.viewmodel.RegistroViewModel
import entystal.view.MainView
import entystal.i18n.I18n
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
      title = I18n("app.title")
      scene = view.scene
    }
    I18n.register(() => stage.title = I18n("app.title"))
  }
}
