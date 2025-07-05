package entystal.util

import entystal.ledger._
import entystal.model._
import zio.{Task, ZIO}

/** Utilidad para exportar el historial del ledger a un fichero CSV */
import java.nio.file.{Files, Path, Paths}

/** Utilidad para exportar el historial del ledger a un fichero CSV */
object CsvExporter {

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
      val writer = new java.io.PrintWriter(valid.toFile)
      try {
        writer.println("type,id,description,timestamp")
        entries.foreach {
          case AssetEntry(asset)           =>
            val desc = asset match {
              case DataAsset(_, data, _, _)        => data
              case CodeAsset(_, repo, _, _)        => repo
              case ReputationAsset(_, score, _, _) => s"score: $score"
            }
            writer.println(s"asset,${asset.id},$desc,${asset.timestamp}")
          case LiabilityEntry(liability)   =>
            val desc = liability match {
              case BasicLiability(_, _, _)        => "basic"
              case EthicalLiability(_, d, _, _)   => d
              case StrategicLiability(_, r, _, _) => r
              case LegalLiability(_, l, _, _)     => l
            }
            writer.println(s"liability,${liability.id},$desc,${liability.timestamp}")
          case InvestmentEntry(investment) =>
            val desc = investment match {
              case BasicInvestment(_, q, _)       => q.toString
              case EconomicInvestment(_, q, _)    => q.toString
              case HumanInvestment(_, q, _)       => q.toString
              case OperationalInvestment(_, q, _) => q.toString
            }
            writer.println(s"investment,${investment.id},$desc,${investment.timestamp}")
        }
      } finally writer.close()
    }
}
