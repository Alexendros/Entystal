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
import entystal.ledger.{AssetEntry, InvestmentEntry, Ledger, LedgerEntry, LiabilityEntry}
import entystal.view.EdicionView
import zio.Runtime

/** Muestra el historial con campo de búsqueda */
class BusquedaView(ledger: Ledger)(implicit runtime: Runtime[Any]) {
  val buscarField = new TextField() {
    promptText = "ID..."
    accessibleText = "Buscar por ID"
  }

  private val buscarTooltip = new Tooltip("Buscar por ID")
  private val buscarBtn     = new Button("Buscar") {
    onAction = _ => cargar(buscarField.text.value)
    tooltip = buscarTooltip
  }

  val tabla = new TableView[LedgerEntry]() {
    columnResizePolicy = TableView.ConstrainedResizePolicy
    columns ++= Seq(
      new TableColumn[LedgerEntry, String]("ID")            {
        cellValueFactory = c => ObjectProperty(c.value.id)
      },
      new TableColumn[LedgerEntry, String]("Tipo")          {
        cellValueFactory = c =>
          ObjectProperty(c.value match {
            case _: AssetEntry      => "activo"
            case _: LiabilityEntry  => "pasivo"
            case _: InvestmentEntry => "inversion"
          })
      },
      new TableColumn[LedgerEntry, String]("Fecha")         {
        cellValueFactory = c => ObjectProperty(c.value.timestamp.toString)
      },
      new TableColumn[LedgerEntry, LedgerEntry]("Acciones") {
        cellValueFactory = c => ObjectProperty(c.value)
        cellFactory = { _: TableColumn[LedgerEntry, LedgerEntry] =>
          new TableCell[LedgerEntry, LedgerEntry] {
            val editarTooltip   = new Tooltip("Editar registro")
            val eliminarTooltip = new Tooltip("Eliminar registro")
            val editarBtn       = new Button("Editar")
            val eliminarBtn     = new Button("Eliminar")
            editarBtn.tooltip = editarTooltip
            eliminarBtn.tooltip = eliminarTooltip
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
    tabla.items = ObservableBuffer.from(filtrados)
  }

  cargar()

  val root = new VBox(10, new HBox(5, buscarField, buscarBtn), tabla)
}
