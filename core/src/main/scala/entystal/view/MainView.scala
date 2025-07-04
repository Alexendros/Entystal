package entystal.view

import scalafx.scene.Scene
import scalafx.scene.control.{Button, ChoiceBox, Label, Tab, TabPane, TextField}
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.collections.ObservableBuffer
import scalafx.Includes._
import scalafx.geometry.Insets
import entystal.viewmodel.RegistroViewModel
import entystal.ledger.Ledger
import zio.Runtime

/** Vista principal de registro */
class MainView(vm: RegistroViewModel, ledger: Ledger)(implicit runtime: Runtime[Any]) {
  private val labelDescripcion = new Label("Descripción")

  private val tipoChoice =
    new ChoiceBox[String](ObservableBuffer("activo", "pasivo", "inversion")) {
      value <==> vm.tipo
      accessibleText = "Tipo de registro"
      focusTraversable = true
    }

  private val idField = new TextField() {
    text <==> vm.identificador
    promptText = I18n("prompt.id")
  }

  private val descField = new TextField() {
    text <==> vm.descripcion
    promptText = I18n("prompt.desc")
  }

  private val registrarBtn = new Button("Registrar") {
    disable <== vm.puedeRegistrar.not()
    onAction = _ => vm.registrar()
  }

  tipoChoice.value.onChange { (_, _, nv) =>
    updateTexts()
  }

  darkModeSwitch.selected.onChange { (_, _, nv) =>
    val theme = if (nv) ThemeManager.Dark else ThemeManager.Light
    ThemeManager.applyTheme(scene, theme)
  }

  private val registroPane = new VBox(10) {
    padding = Insets(20)
    children = Seq(
      new GridPane {
        hgap = 10
        vgap = 10
        add(labelTipo, 0, 0)
        add(tipoChoice, 1, 0)
        add(labelId, 0, 1)
        add(idField, 1, 1)
        add(labelDescripcion, 0, 2)
        add(descField, 1, 2)
      },
      registrarBtn
    )
  }

  private val busquedaView  = new BusquedaView(ledger)
  private val dashboardView = new BusquedaView(ledger)

  private val tabPane = new TabPane {
    tabs = Seq(
      new Tab { text = "Registro"; content = registroPane; closable = false        },
      new Tab { text = "Búsqueda"; content = busquedaView.root; closable = false   },
      new Tab { text = "Dashboard"; content = dashboardView.root; closable = false }
    )
  }

  private val busquedaView  = new BusquedaView(ledger)
  private val dashboardView = new BusquedaView(ledger)

  private val tabPane = new TabPane {
    tabs = Seq(
      new Tab { text = "Registro"; content = registroPane; closable = false        },
      new Tab { text = "Búsqueda"; content = busquedaView.root; closable = false   },
      new Tab { text = "Dashboard"; content = dashboardView.root; closable = false }
    )
  }

  val scene = new Scene(600, 400) {
    root = tabPane
  }
}
