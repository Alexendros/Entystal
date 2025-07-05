package entystal.util

import entystal.ledger._
import entystal.model._
import zio.{Task, ZIO}
import org.apache.pdfbox.pdmodel.{PDDocument, PDPage}
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.PDPageContentStream
import java.nio.file.{Files, Path, Paths}

/** Utilidad para exportar el historial del ledger a un PDF sencillo */
object PdfExporter {

  /** Directorio permitido para las exportaciones */
  val baseDir: Path = {
    val dir = Paths.get("target", "exports").toAbsolutePath.normalize
    Files.createDirectories(dir)
    dir
  }

  private def validatePath(path: String): Path = {
    val p = Paths.get(path).toAbsolutePath.normalize
    if (!p.startsWith(baseDir))
      throw new IllegalArgumentException(s"Ruta fuera de directorio permitido: $path")
    p
  }

  def save(entries: List[LedgerEntry], path: String): Task[Unit] =
    ZIO.attempt {
      val valid  = validatePath(path)
      val doc    = new PDDocument()
      val page   = new PDPage(PDRectangle.LETTER)
      doc.addPage(page)
      val stream = new PDPageContentStream(doc, page)
      try {
        stream.beginText()
        stream.setFont(PDType1Font.HELVETICA, 12)
        stream.newLineAtOffset(50, page.getMediaBox.getHeight - 50)
        entries.zipWithIndex.foreach { case (e, idx) =>
          val line = e match {
            case AssetEntry(asset)    => s"Asset: ${asset.id}"
            case LiabilityEntry(liab) => s"Liability: ${liab.id}"
            case InvestmentEntry(inv) => s"Investment: ${inv.id}"
          }
          if (idx > 0) stream.newLineAtOffset(0, -15)
          stream.showText(line)
        }
        stream.endText()
      } finally {
        stream.close()
        doc.save(valid.toString)
        doc.close()
      }
    }
}
