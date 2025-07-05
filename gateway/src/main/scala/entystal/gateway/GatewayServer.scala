package entystal.gateway

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Host
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.stackstate.akka.http.pac4j.{CallbackDirective, SecurityDirectives}
import org.pac4j.core.client.Clients
import org.pac4j.core.config.Config
import org.pac4j.saml.client.SAML2Client
import org.pac4j.saml.config.SAML2Configuration

import scala.concurrent.Future
import scala.util.{Failure, Success}

/** Servidor de puerta de entrada con Akka HTTP */
object GatewayServer extends App {
  implicit val system = ActorSystem("gateway-system")
  implicit val ec     = system.dispatcher

  private val samlConfig = new SAML2Configuration(
    sys.env.getOrElse("SAML_IDP_METADATA", "idp-metadata.xml"),
    sys.env.getOrElse("SAML_SP_KEYSTORE", "keystore.jks"),
    sys.env.getOrElse("SAML_KEYSTORE_PASSWORD", "changeit"),
    sys.env.getOrElse("SAML_PRIVATEKEY_PASSWORD", "changeit")
  )
  private val samlClient = new SAML2Client(samlConfig)
  private val clients    = new Clients("http://localhost:9000/callback", samlClient)
  private val config     = new Config(clients)
  private val security   = new SecurityDirectives(config)
  private val callback   = new CallbackDirective(config)

  private def forward(req: HttpRequest): Future[HttpResponse] = {
    val proxied = req
      .withUri("http://localhost:8080" + req.uri.path.toString())
      .withHeaders(req.headers.filterNot(_.is("host")))
    Http().singleRequest(proxied)
  }

  private val proxyRoutes: Route = extractClientIP { ip =>
    if (!RateLimiter.allow(ip.toOption.map(_.getHostAddress).getOrElse("unknown")))
      complete(StatusCodes.TooManyRequests, "Límite superado")
    else extractRequest { r => complete(forward(r)) }
  }

  private val routes: Route =
    path("callback") {
      callback()
    } ~
      security.authenticated("SAML2Client") {
        proxyRoutes
      }

  Http().newServerAt("0.0.0.0", 9000).bind(routes).onComplete {
    case Success(binding) =>
      system.log.info(s"Gateway escuchando en ${binding.localAddress}")
    case Failure(err)     =>
      system.log.error("No se pudo iniciar el gateway", err)
      system.terminate()
  }
}
