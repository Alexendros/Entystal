package entystal.rest

import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import org.http4s.{HttpRoutes, Request, Response}
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.{SpanKind, StatusCode}

object Tracing {
  private val tracer = GlobalOpenTelemetry.getTracer("entystal-rest")

  def middleware(routes: HttpRoutes[IO]): HttpRoutes[IO] = Kleisli { req: Request[IO] =>
    OptionT {
      for {
        span   <- IO.blocking(tracer.spanBuilder(s"${req.method.name} ${req.uri.path}")
                      .setSpanKind(SpanKind.SERVER)
                      .startSpan())
        result <- routes(req).value.attempt
        _ <- result match {
              case Right(Some(_)) => IO(span.setStatus(StatusCode.OK))
              case Right(None)    => IO.unit
              case Left(e)        => IO(span.recordException(e)) *> IO(span.setStatus(StatusCode.ERROR))
            }
        _ <- IO(span.end())
        out <- result.fold(IO.raiseError[Option[Response[IO]]], IO.pure)
      } yield out
    }
  }
}
