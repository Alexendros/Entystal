package entystal.security

import com.bettercloud.vault.{Vault, VaultConfig}
import scala.jdk.CollectionConverters._

/** Acceso a secretos almacenados en HashiCorp Vault */
object SecretManager {
  private lazy val client: Option[Vault] =
    for {
      addr  <- sys.env.get("VAULT_ADDR")
      token <- sys.env.get("VAULT_TOKEN")
    } yield new Vault(new VaultConfig().address(addr).token(token).build())

  private def read(path: String): Map[String, String] =
    client
      .flatMap { v =>
        try Option(v.logical().read(path).getData.asScala.toMap)
        catch { case _: Throwable => None }
      }
      .getOrElse(Map.empty)

  def jwtSecret: Option[String] = read("secret/data/jwt").get("secret")

  def dbCredentials: Option[(String, String)] = {
    val data = read("secret/data/database")
    for {
      user <- data.get("username")
      pass <- data.get("password")
    } yield (user, pass)
  }
}
