package entystal.view

import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.layout.VBox
import entystal.i18n.I18n
import entystal.ledger._
import entystal.model.{DataAsset, EthicalLiability, BasicInvestment}

/** Formulario simple para editar un registro. Acciones reales pendientes */
class EdicionView(entry: LedgerEntry) {
  private val titulo   = new Label(s"${I18n("title.editar")} ${entry.id}")
  val campo: TextField = new TextField()
  campo.text = entry match {
    case AssetEntry(a: DataAsset)            => a.data
    case LiabilityEntry(l: EthicalLiability) => l.description
    case InvestmentEntry(i: BasicInvestment) => i.quantity.toString
    case _                                   => ""
  }
  val guardarBtn       = new Button(I18n("button.guardar"))
  val root = new VBox(10, titulo, campo, guardarBtn)
}
