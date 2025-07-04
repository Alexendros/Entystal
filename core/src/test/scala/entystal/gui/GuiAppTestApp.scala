package entystal.gui

import javafx.application.Application
import javafx.stage.Stage

/** Lanzador de GuiApp compatible con TestFX */
class GuiAppTestApp extends Application {
  override def start(primaryStage: Stage): Unit = {
    GuiApp.start()
    primaryStage.setScene(GuiApp.stage.scene.value)
    primaryStage.setTitle(GuiApp.stage.title.value)
    primaryStage.show()
  }
}
