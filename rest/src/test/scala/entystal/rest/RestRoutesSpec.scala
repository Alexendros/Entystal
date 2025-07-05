package entystal.rest

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.syntax.semigroupk._
import org.http4s._
import org.http4s.implicits._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import entystal.service.RegistroService
import entystal.EntystalModule
import entystal.analytics.TrendPredictor
import entystal.viewmodel.RegistroData
import entystal.ledger._
import io.circe.generic.auto._
import org.http4s.circe._
import zio.{Runtime, Unsafe}

class RestRoutesSpec extends AnyFlatSpec with Matchers {
  implicit val runtime: Runtime[Any]                            = Runtime.default
  implicit val registroEncoder: EntityEncoder[IO, RegistroData] = jsonEncoderOf[IO, RegistroData]

  private val ledger  = Unsafe.unsafe { implicit u =>
    runtime.unsafe.run(zio.ZIO.scoped(EntystalModule.layer.build.map(_.get))).getOrThrow()
  }
  private val service    = new RegistroService(ledger)
  private val predictor  = new TrendPredictor(ledger)
  private val app        = (new RestRoutes(service, predictor).routes <+> Metrics.routes).orNotFound

  "POST /registro" should "guardar y consultar" in {
    val req  =
      Request[IO](Method.POST, uri"/registro").withEntity(RegistroData("activo", "a1", "desc"))
    val resp = app.run(req).unsafeRunSync()
    resp.status shouldBe Status.Ok

    val histReq  = Request[IO](Method.GET, uri"/historial")
    val histResp = app.run(histReq).unsafeRunSync()
    val body     = histResp.as[String].unsafeRunSync()
    body.contains("a1") shouldBe true
  }

  "GET /metrics" should "exponer metricas" in {
    val req  = Request[IO](Method.GET, uri"/metrics")
    val resp = app.run(req).unsafeRunSync()
    resp.status shouldBe Status.Ok
  }

  "GET /predicciones" should "devolver analisis" in {
    val postReq =
      Request[IO](Method.POST, uri"/registro").withEntity(RegistroData("activo", "a2", "5"))
    app.run(postReq).unsafeRunSync()

    val req  = Request[IO](Method.GET, uri"/predicciones")
    val resp = app.run(req).unsafeRunSync()
    resp.status shouldBe Status.Ok
    val body = resp.as[String].unsafeRunSync()
    body.contains("a2") shouldBe true
  }
}
