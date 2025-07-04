package entystal.util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import entystal.model._
import entystal.ledger._
import zio.Runtime

class CsvExporterSpec extends AnyFlatSpec with Matchers {
  "CsvExporter" should "generar un fichero con cabecera" in {
    val entries = List(AssetEntry(DataAsset("a1", "info", 1L, BigDecimal(1))))
    val tmp     = java.nio.file.Files.createTempFile("hist", ".csv")
    val rt = Runtime.default
    zio.Unsafe.unsafe { implicit u =>
      rt.unsafe
        .run(CsvExporter.save(entries, tmp.toString))
        .getOrThrow()
    }
    val lines = scala.io.Source.fromFile(tmp.toFile).getLines().toList
    lines.head shouldBe "type,id,description,timestamp"
    tmp.toFile.delete()
  }
}
