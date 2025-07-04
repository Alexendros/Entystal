package entystal.gui

import scalafx.application.JFXApp3
import scalafx.stage.Stage
import entystal.{EntystalModule}
import entystal.ledger.Ledger
import entystal.viewmodel.RegistroViewModel
import entystal.service.RegistroService
import entystal.view.MainView
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
    val service                        = new RegistroService(ledger)
    val vm                             = new RegistroViewModel(service)
    val view                           = new MainView(vm)

    stage = new JFXApp3.PrimaryStage {
      title = "Entystal GUI"
      scene = view.scene
    }
  }
}
