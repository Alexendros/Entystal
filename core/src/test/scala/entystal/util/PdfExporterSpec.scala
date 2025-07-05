package entystal.util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import entystal.model._
import entystal.ledger._
import org.apache.pdfbox.pdmodel.PDDocument
import zio.Runtime

class PdfExporterSpec extends AnyFlatSpec with Matchers {
  "PdfExporter" should "crear un PDF no vacío" in {
    val entries = List(LiabilityEntry(BasicLiability("l1", BigDecimal(1), 2L)))
    val tmp     = java.nio.file.Files.createTempFile(PdfExporter.baseDir, "hist", ".pdf")
    val rt      = Runtime.default
    zio.Unsafe.unsafe { implicit u =>
      rt.unsafe
        .run(PdfExporter.save(entries, tmp.toString))
        .getOrThrow()
    }
    val doc     = PDDocument.load(tmp.toFile)
    try doc.getNumberOfPages should be > 0
    finally { doc.close(); tmp.toFile.delete() }
  }
}
