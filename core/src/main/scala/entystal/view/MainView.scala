package entystal.view

import scalafx.scene.Scene
import scalafx.scene.control.{Button, ChoiceBox, Label, TextField, CheckBox}
import scalafx.scene.layout.{GridPane, VBox}
import scalafx.collections.ObservableBuffer
import scalafx.Includes._
import scalafx.geometry.Insets
import entystal.viewmodel.RegistroViewModel
import entystal.gui.ThemeManager

/** Vista principal de registro */
class MainView(vm: RegistroViewModel) {
  private val labelTipo        = new Label()
  private val labelId          = new Label()
  private val labelDescripcion = new Label()
  private val langChoice =
    new ChoiceBox[String](ObservableBuffer("es", "en")) {
      value = I18n.locale.value.getLanguage
    }

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

  val rootPane = new VBox(10) {
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

  val scene = new Scene(400, 200) {
    root = rootPane
    // Cargar tema guardado al crear la vista
    stylesheets += ThemeManager.loadTheme().css
  }
}
