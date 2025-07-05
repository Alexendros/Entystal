package entystal.auth

import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import org.http4s.{HttpRoutes, Request}
import org.http4s.dsl.io._
import org.http4s.headers.Authorization
import org.http4s.{AuthScheme, Credentials}

object JwtMiddleware {
  private val Bearer = "Bearer "

  def apply(routes: HttpRoutes[IO]): HttpRoutes[IO] = Kleisli { (req: Request[IO]) =>
    val valid = req.headers
      .get[Authorization]
      .collect { case Authorization(Credentials.Token(AuthScheme.Bearer, token)) =>
        AuthService.validateToken(token)
      }
      .getOrElse(false)

    if (valid) routes(req)
    else OptionT.liftF(Forbidden("Token inválido"))
  }
}
