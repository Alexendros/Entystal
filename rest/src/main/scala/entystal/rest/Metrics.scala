package entystal.rest

import cats.effect.IO
import io.prometheus.client.{CollectorRegistry, Counter}
import io.prometheus.client.exporter.common.TextFormat
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object Metrics {
  private val requests: Counter =
    Counter.build().name("rest_requests_total").help("Total de peticiones").register()

  def record(): IO[Unit] = IO(requests.inc())

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] { case GET -> Root / "metrics" =>
    val writer = new java.io.StringWriter()
    TextFormat.write004(writer, CollectorRegistry.defaultRegistry.metricFamilySamples())
    Ok(writer.toString())
  }
}
