package entystal.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import entystal.security.SecretManager
import java.util.Date

object AuthService {
  private val secret =
    SecretManager.jwtSecret.getOrElse(sys.env.getOrElse("JWT_SECRET", "entystal-secret"))

  private val algo = Algorithm.HMAC256(secret)

  def generateToken(user: String): String =
    JWT
      .create()
      .withSubject(user)
      .withIssuedAt(new Date())
      .withExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000))
      .sign(algo)

  def validateToken(token: String): Boolean =
    try {
      JWT.require(algo).build().verify(token)
      true
    } catch {
      case _: Throwable => false
    }
}
