package entystal.rest

import cats.effect.{IO, IOApp}
import cats.syntax.semigroupk._
import org.http4s.server.Router
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.SSLClientAuthMode
import com.comcast.ip4s._
import entystal.service.RegistroService
import entystal.EntystalModule
import entystal.auth.JwtMiddleware
import java.io.FileInputStream
import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}
import zio.{Runtime, Unsafe}

/** Servidor HTTP básico para registrar eventos y consultar historial */
object RestServer extends IOApp.Simple {
  override def run: IO[Unit] = {
    implicit val runtime: Runtime[Any] = Runtime.default
    val ledger                         = Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(zio.ZIO.scoped(EntystalModule.layer.build.map(_.get))).getOrThrow()
    }
    val service                        = new RegistroService(ledger)
    val api                            = JwtMiddleware(new RestRoutes(service).routes <+> Metrics.routes)

    val sslContext = for {
      ksPath <- sys.env.get("HTTPS_KEYSTORE_PATH")
      ksPass <- sys.env.get("HTTPS_KEYSTORE_PASSWORD")
      tsPath <- sys.env.get("HTTPS_TRUSTSTORE_PATH")
      tsPass <- sys.env.get("HTTPS_TRUSTSTORE_PASSWORD")
    } yield {
      val ks  = KeyStore.getInstance("PKCS12")
      val fis = new FileInputStream(ksPath)
      ks.load(fis, ksPass.toCharArray)
      fis.close()

      val trust = KeyStore.getInstance("PKCS12")
      val tis   = new FileInputStream(tsPath)
      trust.load(tis, tsPass.toCharArray)
      tis.close()

      val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
      kmf.init(ks, ksPass.toCharArray)
      val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm)
      tmf.init(trust)

      val ctx = SSLContext.getInstance("TLS")
      ctx.init(kmf.getKeyManagers, tmf.getTrustManagers, new SecureRandom())
      ctx
    }

    val builder = BlazeServerBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .withHttpApp(Router("/" -> api).orNotFound)

    sslContext
      .fold(builder)(ctx => builder.withSSLContext(ctx, SSLClientAuthMode.Required))
      .resource
      .useForever
  }
}
