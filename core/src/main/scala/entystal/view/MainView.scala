package entystal.view

import scalafx.scene.Scene
import scalafx.scene.control.{Button, ChoiceBox, Label, Tab, TabPane, TextField}
import scalafx.scene.layout.{GridPane, VBox}
import scalafx.collections.ObservableBuffer
import scalafx.Includes._
import scalafx.geometry.Insets
import entystal.viewmodel.RegistroViewModel
import entystal.gui.ThemeManager
import entystal.i18n.I18n
import entystal.ledger.Ledger
import entystal.i18n.I18n
import zio.Runtime
import java.util.Locale

/** Vista principal con pestañas de registro y búsqueda */

class MainView(vm: RegistroViewModel, ledger: Ledger)(implicit runtime: Runtime[Any]) {
  private val labelTipo        = new Label()
  private val labelId          = new Label()
  private val labelDescripcion = new Label()
  private val langChoice       = new ChoiceBox[String](ObservableBuffer("es", "en")) {
    value = I18n.locale.value.getLanguage
  }
  private val tipoChoice       =
    new ChoiceBox[String](ObservableBuffer("activo", "pasivo", "inversion")) {
      value <==> vm.tipo
    }
  val idField   = new TextField() { text <==> vm.identificador }
  val descField = new TextField() { text <==> vm.descripcion }
  val registrarBtn = new Button() {
    disable <== vm.puedeRegistrar.not()
    mnemonicParsing = true
    onAction = _ => vm.registrar()
  }

  tipoChoice.value.onChange { (_, _, _) => updateTexts() }
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
    idField.accessibleText = labelId.text.value
    descField.accessibleText = labelDescripcion.text.value
    tipoChoice.accessibleText = labelTipo.text.value
    langChoice.accessibleText = "Idioma"
    registrarBtn.text = "_" + I18n("button.registrar")
    registrarBtn.accessibleText = I18n("button.registrar")
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

  val scene = new Scene(600, 400) {
    root = tabPane
    stylesheets += ThemeManager.loadTheme().css
  }
}
