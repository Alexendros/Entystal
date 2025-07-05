package entystal.view

import scalafx.scene.control.{
  Button,
  ContentDisplay,
  TableCell,
  TableColumn,
  TableView,
  TextField,
  Tooltip
}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.Stage
import scalafx.scene.Scene
import scalafx.collections.ObservableBuffer
import scalafx.beans.property.ObjectProperty
import scalafx.Includes._
import scalafx.application.Platform
import entystal.ledger.{AssetEntry, InvestmentEntry, Ledger, LedgerEntry, LiabilityEntry}
import entystal.view.EdicionView
import entystal.i18n.I18n
import zio.Runtime

/** Muestra el historial con campo de búsqueda */
class BusquedaView(ledger: Ledger)(implicit runtime: Runtime[Any]) {
  private val buscarField = new TextField() {
    promptText = s"${I18n("prompt.id")}" + "..."
  }

  private val buscarTooltip = new Tooltip()

  val buscarBtn = new Button(I18n("button.buscar")) {
    onAction = _ => cargar(buscarField.text.value)
    tooltip = buscarTooltip
  }

  val tabla = new TableView[LedgerEntry]() {
    columnResizePolicy = TableView.ConstrainedResizePolicy
    columns ++= Seq(
      new TableColumn[LedgerEntry, String](I18n("column.id"))            {
        cellValueFactory = c => ObjectProperty(c.value.id)
      },
      new TableColumn[LedgerEntry, String](I18n("column.tipo"))          {
        cellValueFactory = c =>
          ObjectProperty(c.value match {
            case _: AssetEntry      => "activo"
            case _: LiabilityEntry  => "pasivo"
            case _: InvestmentEntry => "inversion"
          })
      },
      new TableColumn[LedgerEntry, String](I18n("column.fecha"))         {
        cellValueFactory = c => ObjectProperty(c.value.timestamp.toString)
      },
      new TableColumn[LedgerEntry, LedgerEntry](I18n("column.acciones")) {
        cellValueFactory = c => ObjectProperty(c.value)
        cellFactory = { _: TableColumn[LedgerEntry, LedgerEntry] =>
          new TableCell[LedgerEntry, LedgerEntry] {
            val editarBtn   = new Button(I18n("button.editar"))
            val eliminarBtn = new Button(I18n("button.eliminar"))
            contentDisplay = ContentDisplay.GraphicOnly
            graphic = new HBox(5, editarBtn, eliminarBtn)

            editarBtn.onAction = _ => {
              val entry = item()
              if (entry != null) {
                val view  = new EdicionView(entry)
                val stage = new Stage { scene = new Scene(view.root) }
                view.guardarBtn.onAction = _ => {
                  val nuevo = view.campo.text.value
                  zio.Unsafe.unsafe { implicit u =>
                    runtime.unsafe.run(ledger.updateEntry(entry.id, nuevo)).getOrThrow()
                  }
                  stage.close()
                  cargar()
                }
                stage.showAndWait()
              }
            }

            eliminarBtn.onAction = _ => {
              val entry = item()
              if (entry != null) {
                zio.Unsafe.unsafe { implicit u =>
                  runtime.unsafe.run(ledger.deleteEntry(entry.id)).getOrThrow()
                }
                cargar()
              }
            }
          }
        }
      }
    )
  }

  def cargar(filtro: String = ""): Unit = {
    val datos     = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(ledger.getHistory).getOrThrow()
    }
    val filtrados =
      if (filtro.trim.isEmpty) datos
      else datos.filter(_.id.contains(filtro.trim))
    Platform.runLater {
      tabla.items = ObservableBuffer.from(filtrados)
    }
  }

  cargar()

  val root = new VBox(10, new HBox(5, buscarField, buscarBtn), tabla)
}
