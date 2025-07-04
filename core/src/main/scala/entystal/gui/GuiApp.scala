package entystal.gui

import scalafx.application.JFXApp3
import scalafx.stage.Stage
import entystal.{EntystalModule}
import entystal.ledger.Ledger
import entystal.viewmodel.RegistroViewModel
import entystal.view.MainView
import zio.Runtime

/** Lanzador principal de la interfaz gráfica */
object GuiApp extends JFXApp3 {

  /** Ledger utilizado por la aplicación, visible para pruebas */
  private[gui] var appLedger: Ledger = _

  /** ViewModel utilizado por la aplicación, visible para pruebas */
  private[gui] var appViewModel: RegistroViewModel = _

  override def start(): Unit = {
    implicit val runtime: Runtime[Any] = Runtime.default
    val ledger: Ledger                 = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe
        .run(zio.ZIO.scoped(EntystalModule.layer.build.map(_.get)))
        .getOrThrow()
    }
    appLedger = ledger
    val vm                             = new RegistroViewModel(ledger)
    appViewModel = vm
    val view                           = new MainView(vm)

    stage = new JFXApp3.PrimaryStage {
      title = "Entystal GUI"
      scene = view.scene
    }
  }
}
