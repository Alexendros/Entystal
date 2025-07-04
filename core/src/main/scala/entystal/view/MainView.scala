package entystal.view

import scalafx.scene.Scene
import scalafx.scene.control.{Button, ChoiceBox, Label, TextField, TabPane, Tab}
import scalafx.scene.layout.{GridPane, VBox}
import scalafx.collections.ObservableBuffer
import scalafx.Includes._
import scalafx.geometry.Insets
import entystal.viewmodel.RegistroViewModel

/** Vista principal de registro */
class MainView(vm: RegistroViewModel, dashboard: DashboardView) {
  private val labelDescripcion = new Label("Descripción")

  private val tipoChoice =
    new ChoiceBox[String](ObservableBuffer("activo", "pasivo", "inversion")) {
      value <==> vm.tipo
    }

  private val idField = new TextField() {
    text <==> vm.identificador
    promptText = "ID"
  }

  private val descField = new TextField() {
    text <==> vm.descripcion
    promptText = "Descripción o cantidad"
  }

  private val mensajeLabel = new Label()

  private val registrarBtn = new Button("Registrar") {
    disable <== vm.puedeRegistrar.not()
    onAction = _ => {
      mensajeLabel.text = vm.registrar()
      dashboard.refresh()
    }
  }

  tipoChoice.value.onChange { (_, _, nv) =>
    labelDescripcion.text = if (nv == "inversion") "Cantidad" else "Descripción"
  }

  private val registroPane = new VBox(10) {
    padding = Insets(20)
    children = Seq(
      new GridPane {
        hgap = 10
        vgap = 10
        add(new Label("Tipo"), 0, 0)
        add(tipoChoice, 1, 0)
        add(new Label("ID"), 0, 1)
        add(idField, 1, 1)
        add(labelDescripcion, 0, 2)
        add(descField, 1, 2)
      },
      registrarBtn,
      mensajeLabel
    )
  }

  val rootPane = new TabPane {
    tabs = Seq(
      new Tab {
        text = "Registro"
        content = registroPane
        closable = false
      },
      new Tab {
        text = "Dashboard"
        content = dashboard.rootPane
        closable = false
      }
    )
  }

  val scene = new Scene(400, 300) {
    root = rootPane
  }
}
