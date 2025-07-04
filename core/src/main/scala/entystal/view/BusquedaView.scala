package entystal.view

import scalafx.scene.control.{Button, ContentDisplay, TableCell, TableColumn, TableView, TextField}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.collections.ObservableBuffer
import scalafx.beans.property.ObjectProperty
import scalafx.Includes._
import entystal.ledger.{AssetEntry, InvestmentEntry, Ledger, LedgerEntry, LiabilityEntry}
import zio.Runtime

/** Muestra el historial con campo de búsqueda */
class BusquedaView(ledger: Ledger)(implicit runtime: Runtime[Any]) {
  private val buscarField = new TextField() {
    promptText = "ID..."
  }

  private val buscarBtn = new Button("Buscar") {
    onAction = _ => cargar(buscarField.text.value)
  }

  private val tabla = new TableView[LedgerEntry]() {
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
            val editarBtn   = new Button("Editar")
            val eliminarBtn = new Button("Eliminar")
            editarBtn.onAction = _ => println(s"Editar ${item.value.id}")
            eliminarBtn.onAction = _ => println(s"Eliminar ${item.value.id}")
            contentDisplay = ContentDisplay.GraphicOnly
            graphic = new HBox(5, editarBtn, eliminarBtn)
          }
        }
      }
    )
  }

  private def cargar(filtro: String = ""): Unit = {
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
