package entystal.cli

import entystal._
import entystal.ledger._
import entystal.model._
import entystal.service.RegistroService
import entystal.viewmodel.RegistroData
import entystal.InputValidators
import zio._
import scopt.OParser

case class Config(
    assetId: Option[String] = None,
    assetDesc: Option[String] = None,
    liabilityDesc: Option[String] = None,
    investmentQty: Option[BigDecimal] = None,
    mode: String = "asset"
)

object Main extends ZIOAppDefault {
  private val validator = InputValidators.RegistroDataValidator
  val builder           = OParser.builder[Config]
  val parser            = {
    import builder._
    OParser.sequence(
      programName("entystal-cli"),
      head("entystal", "0.1"),
      opt[String]("assetId").action((x, c) => c.copy(assetId = Some(x))),
      opt[String]("assetDesc").action((x, c) => c.copy(assetDesc = Some(x))),
      opt[String]("liabilityDesc").action((x, c) => c.copy(liabilityDesc = Some(x))),
      opt[BigDecimal]("investmentQty").action((x, c) => c.copy(investmentQty = Some(x))),
      opt[String]("mode").action((x, c) => c.copy(mode = x))
    )
  }

  def run =
    ZIOAppArgs.getArgs.flatMap { appArgs =>
      OParser.parse(parser, appArgs.toList, Config()) match {
        case Some(cfg) =>
          val tipo = cfg.mode match {
            case "asset"      => "activo"
            case "liability"  => "pasivo"
            case "investment" => "inversion"
            case other        => other
          }
          val desc = cfg.mode match {
            case "asset"      => cfg.assetDesc.getOrElse("")
            case "liability"  => cfg.liabilityDesc.getOrElse("")
            case "investment" => cfg.investmentQty.map(_.toString).getOrElse("")
            case _            => ""
          }
          val data = RegistroData(tipo, cfg.assetId.getOrElse(""), desc)
          validator.validate(data) match {
            case Left(err) => Console.printLine(err)
            case Right(_)  =>
              for {
                ledger <- EntystalModule.layer.build.map(_.get)
                service = new RegistroService(ledger)
                _      <- service.registrar(data)
                _      <- Console.printLine(s"Registrado ${data.tipo}: ${data.identificador}")
              } yield ()
          }
        case _         =>
          Console.printLine("Parámetros insuficientes o incorrectos.")
      }
    }
}
