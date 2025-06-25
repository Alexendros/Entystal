package entystal.cli

import entystal._
import entystal.ledger._
import entystal.model._
import zio._
import scopt.OParser

case class Config(
  assetId: Option[String] = None,
  assetDesc: Option[String] = None,
  mode: String = "asset"
)

object Main extends ZIOAppDefault {
  val builder = OParser.builder[Config]
  val parser = {
    import builder._
    OParser.sequence(
      programName("entystal-cli"),
      head("entystal", "0.1"),
      opt[String]("assetId").action((x, c) => c.copy(assetId = Some(x))),
      opt[String]("assetDesc").action((x, c) => c.copy(assetDesc = Some(x))),
      opt[String]("mode").action((x, c) => c.copy(mode = x))
    )
  }

  def run = {
    val argsArray = Option(System.getenv("ARGS")).fold(Array.empty[String])(_.split(" "))
    OParser.parse(parser, argsArray, Config()) match {
      case Some(cfg) if cfg.mode == "asset" && cfg.assetId.nonEmpty && cfg.assetDesc.nonEmpty =>
        for {
          ledger <- EntystalModule.layer.build.map(_.get)
          ts     = System.currentTimeMillis
          asset  = DataAsset(cfg.assetId.get, cfg.assetDesc.get, ts, BigDecimal(1))
          _      <- ledger.recordAsset(asset)
          _      <- Console.printLine(s"Registrado activo: $asset")
        } yield ()
      case _ =>
        Console.printLine("Par\u00e1metros insuficientes o incorrectos.")
    }
  }
}
