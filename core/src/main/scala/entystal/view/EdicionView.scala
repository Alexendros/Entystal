package entystal.view

import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.layout.VBox
import entystal.ledger.LedgerEntry

/** Formulario simple para editar un registro. Acciones reales pendientes */
class EdicionView(entry: LedgerEntry) {
  private val titulo = new Label(s"Editar ${entry.id}")
  private val campo  = new TextField()
  val guardarBtn     = new Button("Guardar") {
    onAction = _ => println(s"Guardar cambios de ${entry.id}")
  }

  val root = new VBox(10, titulo, campo, guardarBtn)
}
