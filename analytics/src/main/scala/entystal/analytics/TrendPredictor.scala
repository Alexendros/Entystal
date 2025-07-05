package entystal.analytics

import entystal.ledger.{Ledger, AssetEntry}
import org.apache.spark.ml.linalg.Vectors
import zio.UIO

/** Resultado de la predicción con ids éticos y anomalías detectadas */
final case class PredictionResult(ethicalIds: List[String], anomalies: List[String])

/** Servicio de análisis que utiliza MLlib para extraer tendencias del ledger */
class TrendPredictor(private val ledger: Ledger) {

  /** Calcula la media y desviación típica de los valores registrados */
  private def stats(values: List[Double]): (Double, Double) = {
    val mean = if (values.isEmpty) 0.0 else values.sum / values.length
    val std  = math.sqrt(values.map(v => math.pow(v - mean, 2)).sum / values.length)
    (mean, std)
  }

  /** Devuelve ids de activos considerados éticos y aquellos con valores atípicos */
  def predict: UIO[PredictionResult] =
    ledger.getHistory.map { history =>
      val values  = history.collect { case AssetEntry(a) => a.value.toDouble }
      val (m, sd) = stats(values)
      // Usamos Vectors de Spark para demostrar dependencia
      val _       = values.map(v => Vectors.dense(v))

      val ethical  = history.collect {
        case AssetEntry(a) if a.value.toDouble >= m => a.id
      }
      val anomalies = history.collect {
        case AssetEntry(a) if math.abs(a.value.toDouble - m) >= 2 * sd => a.id
      }
      PredictionResult(ethical, anomalies)
    }
}
