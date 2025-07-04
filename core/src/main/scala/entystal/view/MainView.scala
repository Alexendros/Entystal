package entystal.view

import scalafx.scene.Scene
import scalafx.scene.control.{Button, ChoiceBox, Label, Tab, TabPane, TextField, CheckBox}
import scalafx.scene.layout.{GridPane, VBox}
import scalafx.collections.ObservableBuffer
import scalafx.Includes._
import scalafx.geometry.Insets
import entystal.viewmodel.RegistroViewModel
import entystal.gui.ThemeManager
import entystal.i18n.I18n
import entystal.ledger.Ledger
import java.util.Locale
import zio.Runtime
import java.util.Locale

/** Vista principal con registro y pestañas de historial */

class MainView(vm: RegistroViewModel, ledger: Ledger)(implicit runtime: Runtime[Any]) {
  private val labelTipo        = new Label()
  private val labelId          = new Label()
  private val labelDescripcion = new Label()
  private val langChoice       =
    new ChoiceBox[String](ObservableBuffer("es", "en")) {
      value = I18n.locale.value.getLanguage
    }

  private val tipoChoice =
    new ChoiceBox[String](ObservableBuffer("activo", "pasivo", "inversion")) {
      value <==> vm.tipo
    }
  private val idField = new TextField() {
    text <==> vm.identificador
    promptText <== I18n.binding("prompt.id")
  }

  private val descField = new TextField() {
    text <==> vm.descripcion
    promptText <== I18n.binding("prompt.desc")
  }

  private val langChoice = new ChoiceBox[String](ObservableBuffer("es", "en")) {
    value = I18n.locale.value.getLanguage
  }

  private val darkModeSwitch = new CheckBox("Tema oscuro") {
    selected = ThemeManager.loadTheme() == ThemeManager.Dark
  }

  private val registrarBtn = new Button() {
    text <== I18n.binding("button.registrar")
    disable <== vm.puedeRegistrar.not()
    onAction = _ => vm.registrar()
  }

  private def updateTexts(): Unit = {
    labelTipo.text = I18n("label.tipo")
    labelId.text = I18n("label.id")
    labelDescripcion.text =
      if (tipoChoice.value == "inversion") I18n("label.cantidad")
      else I18n("label.descripcion")
  }

  I18n.register(() => updateTexts())
  updateTexts()

  tipoChoice.value.onChange { (_, _, _) => updateTexts() }
  langChoice.value.onChange { (_, _, nv) => if (nv != null) I18n.setLocale(Locale.forLanguageTag(nv)) }
  darkModeSwitch.selected.onChange { (_, _, nv) =>
    val theme = if (nv) ThemeManager.Dark else ThemeManager.Light
    ThemeManager.applyTheme(scene, theme)
  }

  private def updateTexts(): Unit = {
    labelTipo.text = I18n("label.tipo")
    labelId.text = I18n("label.id")
    labelDescripcion.text =
      if (tipoChoice.value == "inversion") I18n("label.cantidad")
      else I18n("label.descripcion")
    idField.promptText = I18n("prompt.id")
    descField.promptText = I18n("prompt.desc")
  }

  I18n.register(() => updateTexts())
  updateTexts()

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
      langChoice,
      registrarBtn,
      new VBox(5)  {
        children = Seq(exportCsvBtn, exportPdfBtn)
      },
      mensajeLabel
    )
  }

  val scene = new Scene(600, 400) {
    root = tabPane
    stylesheets += ThemeManager.loadTheme().css
  }
}
