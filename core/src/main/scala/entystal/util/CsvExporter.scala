package entystal.util

import entystal.ledger._
import entystal.model._
import zio.{Task, ZIO}

/** Utilidad para exportar el historial del ledger a un fichero CSV */
object CsvExporter {
  def save(entries: List[LedgerEntry], path: String): Task[Unit] =
    ZIO.attempt {
      val writer = new java.io.PrintWriter(new java.io.File(path))
      try {
        writer.println("type,id,description,timestamp")
        entries.foreach {
          case AssetEntry(asset) =>
            val desc = asset match {
              case DataAsset(_, data, _, _)        => data
              case CodeAsset(_, repo, _, _)        => repo
              case ReputationAsset(_, score, _, _) => s"score: $score"
            }
            writer.println(s"asset,${asset.id},$desc,${asset.timestamp}")
          case LiabilityEntry(liability) =>
            val desc = liability match {
              case BasicLiability(_, _, _)          => "basic"
              case EthicalLiability(_, d, _, _)     => d
              case StrategicLiability(_, r, _, _)   => r
              case LegalLiability(_, l, _, _)       => l
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
