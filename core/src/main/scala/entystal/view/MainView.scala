package entystal.view

import scalafx.scene.Scene
import scalafx.scene.control.{Button, ChoiceBox, Label, TextField}
import scalafx.scene.layout.{GridPane, VBox}
import scalafx.collections.ObservableBuffer
import scalafx.Includes._
import scalafx.geometry.Insets
import entystal.viewmodel.RegistroViewModel
import entystal.i18n.I18n
import java.util.Locale

/** Vista principal de registro */
class MainView(vm: RegistroViewModel) {
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

  private val mensajeLabel = new Label()

  private val registrarBtn = new Button() {
    disable <== vm.puedeRegistrar.not()
    onAction = _ => mensajeLabel.text = vm.registrar()
    focusTraversable = true
  }

  private val exportCsvBtn = new Button("Exportar CSV") {
    onAction = _ => mensajeLabel.text = vm.exportCsv()
  }

  private val exportPdfBtn = new Button("Exportar PDF") {
    onAction = _ => mensajeLabel.text = vm.exportPdf()
  }

  tipoChoice.value.onChange { (_, _, nv) =>
    updateTexts()
  }

  langChoice.value.onChange { (_, _, nv) =>
    if (nv != null) I18n.setLocale(Locale.forLanguageTag(nv))
  }

  private def updateTexts(): Unit = {
    labelTipo.text = I18n("label.tipo")
    labelId.text = I18n("label.id")
    labelDescripcion.text =
      if (tipoChoice.value == "inversion") I18n("label.cantidad")
      else I18n("label.descripcion")
    idField.promptText = I18n("prompt.id")
    descField.promptText = I18n("prompt.desc")
    registrarBtn.text = I18n("button.registrar")
  }

  I18n.register(() => updateTexts())
  updateTexts()

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
      langChoice,
      registrarBtn,
      new VBox(5)  {
        children = Seq(exportCsvBtn, exportPdfBtn)
      },
      mensajeLabel
    )
  }

  val scene = new Scene(400, 200) {
    root = rootPane
  }
}
