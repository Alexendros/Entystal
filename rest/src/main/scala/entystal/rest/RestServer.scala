package entystal.rest

import cats.effect.{IO, IOApp}
import cats.syntax.semigroupk._
import org.http4s.server.Router
import org.http4s.ember.server.EmberServerBuilder
import com.comcast.ip4s._
import entystal.service.RegistroService
import entystal.EntystalModule
import zio.{Runtime, Unsafe}

/** Servidor HTTP básico para registrar eventos y consultar historial */
object RestServer extends IOApp.Simple {
  override def run: IO[Unit] = {
    implicit val runtime: Runtime[Any] = Runtime.default
    val ledger                         = Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(zio.ZIO.scoped(EntystalModule.layer.build.map(_.get))).getOrThrow()
    }
    val service                        = new RegistroService(ledger)
    val api                            = new RestRoutes(service).routes <+> Metrics.routes

    EmberServerBuilder
      .default[IO]
      .withHost(host"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(Router("/" -> api).orNotFound)
      .build
      .useForever
  }
}
