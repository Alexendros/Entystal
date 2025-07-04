package entystal.viewmodel

import scalafx.beans.property.StringProperty
import scalafx.beans.binding.{BooleanBinding, Bindings}
import entystal.model._
import entystal.ledger.Ledger
import entystal.util.{CsvExporter, PdfExporter}
import zio.Runtime

/** ViewModel para el formulario de registro */
class RegistroViewModel(service: RegistroService, validator: RegistroValidator) {
  val tipo          = StringProperty("activo")
  val identificador = StringProperty("")
  val descripcion   = StringProperty("")

  /** Validación reactiva de los campos */
  val puedeRegistrar: BooleanBinding = Bindings.createBooleanBinding(
    () => validator.validate(RegistroData(tipo.value, identificador.value, descripcion.value)).isRight,
    identificador,
    descripcion,
    tipo,
  )

  /** Devuelve mensaje de error o confirma el registro */
  def registrar(): String =
    validator.validate(RegistroData(tipo.value, identificador.value, descripcion.value)) match {
      case Left(err) => err
      case Right(_)  => service.registrar(RegistroData(tipo.value, identificador.value, descripcion.value))
    }

    val ts = System.currentTimeMillis
    tipo.value match {
      case "activo" =>
        val asset = DataAsset(identificador.value, descripcion.value, ts, BigDecimal(1))
        zio.Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(ledger.recordAsset(asset)).getOrThrow()
        }
      case "pasivo" =>
        val liability = EthicalLiability(identificador.value, descripcion.value, ts, BigDecimal(1))
        zio.Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(ledger.recordLiability(liability)).getOrThrow()
        }
      case _        =>
        val qty        = BigDecimal(descripcion.value)
        val investment = BasicInvestment(identificador.value, qty, ts)
        zio.Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(ledger.recordInvestment(investment)).getOrThrow()
        }
    }
    "Registro completado"
  }

  /** Exporta el historial a CSV y devuelve mensaje de confirmación */
  def exportCsv(path: String = "ledger.csv"): String = {
    val entries = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(ledger.getHistory).getOrThrow()
    }
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(CsvExporter.save(entries, path)).getOrThrow()
    }
    s"CSV exportado en $path"
  }

  /** Exporta el historial a PDF y devuelve mensaje de confirmación */
  def exportPdf(path: String = "ledger.pdf"): String = {
    val entries = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(ledger.getHistory).getOrThrow()
    }
    zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(PdfExporter.save(entries, path)).getOrThrow()
    }
    s"PDF exportado en $path"
  }
}
