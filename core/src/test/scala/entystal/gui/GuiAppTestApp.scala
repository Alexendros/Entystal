package entystal.gui

import javafx.application.Application
import javafx.stage.Stage
import scalafx.application.JFXApp3
import entystal.{EntystalModule}
import entystal.ledger.Ledger
import entystal.viewmodel.RegistroViewModel
import entystal.view.MainView
import zio.Runtime

/** Lanzador de GuiApp compatible con TestFX */
class GuiAppTestApp extends Application {
  override def start(primaryStage: Stage): Unit = {
    implicit val runtime: Runtime[Any] = Runtime.default
    val ledger: Ledger                 = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe
        .run(zio.ZIO.scoped(EntystalModule.layer.build.map(_.get)))
        .getOrThrow()
    }
    GuiApp.appLedger = ledger
    val vm                             = new RegistroViewModel(ledger)
    GuiApp.appViewModel = vm
    val view                           = new MainView(vm)

    primaryStage.setScene(view.scene.delegate)
    primaryStage.setTitle("Entystal GUI")
    primaryStage.show()
  }
}
