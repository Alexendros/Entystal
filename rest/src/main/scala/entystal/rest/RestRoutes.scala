package entystal.rest

import cats.effect.IO
import cats.syntax.all._
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import org.http4s.dsl.io._
import org.http4s.circe._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json}
import io.circe.generic.auto._
import entystal.service.RegistroService
import entystal.analytics.{TrendPredictor, PredictionResult}
import entystal.viewmodel.RegistroData
import entystal.InputValidators
import entystal.ledger._
import zio.{Runtime, Unsafe}

class RestRoutes(service: RegistroService, predictor: TrendPredictor)(implicit runtime: Runtime[Any]) {

  implicit val registroDecoder: EntityDecoder[IO, RegistroData]        = jsonOf[IO, RegistroData]
  implicit val ledgerEntryEncoder: Encoder[LedgerEntry]                = Encoder.instance {
    case AssetEntry(a)      =>
      Json.obj(
        "id"        -> Json.fromString(a.id),
        "timestamp" -> Json.fromLong(a.timestamp),
        "tipo"      -> Json.fromString("activo")
      )
    case LiabilityEntry(l)  =>
      Json.obj(
        "id"        -> Json.fromString(l.id),
        "timestamp" -> Json.fromLong(l.timestamp),
        "tipo"      -> Json.fromString("pasivo")
      )
    case InvestmentEntry(i) =>
      Json.obj(
        "id"        -> Json.fromString(i.id),
        "timestamp" -> Json.fromLong(i.timestamp),
        "tipo"      -> Json.fromString("inversion")
      )
  }
  implicit val ledgerListEncoder: EntityEncoder[IO, List[LedgerEntry]] =
    jsonEncoderOf[IO, List[LedgerEntry]]
  implicit val predictionEncoder: EntityEncoder[IO, PredictionResult] =
    jsonEncoderOf[IO, PredictionResult]

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "registro" =>
      for {
        data <- req.as[RegistroData]
        resp <- InputValidators.RegistroDataValidator.validate(data) match {
                  case Left(err) => BadRequest(err)
                  case Right(_)  =>
                    for {
                      _    <- IO.blocking(Unsafe.unsafe { implicit u =>
                                runtime.unsafe.run(service.registrar(data)).getOrThrow()
                              })
                      _    <- Metrics.record()
                      resp <- Ok("ok")
                    } yield resp
                }
      } yield resp

    case GET -> Root / "historial" =>
      for {
        hist <- IO.blocking(Unsafe.unsafe { implicit u =>
                  runtime.unsafe.run(service.obtenerHistorial).getOrThrow()
                })
        _    <- Metrics.record()
        resp <- Ok(hist.asJson)
      } yield resp

    case GET -> Root / "predicciones" =>
      for {
        pred <- IO.blocking(Unsafe.unsafe { implicit u =>
                  runtime.unsafe.run(predictor.predict).getOrThrow()
                })
        _    <- Metrics.record()
        resp <- Ok(pred.asJson)
      } yield resp
  }
}
