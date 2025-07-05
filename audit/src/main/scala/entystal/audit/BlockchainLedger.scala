package entystal.audit

import entystal.ledger._
import entystal.model._
import zio.{Ref, UIO, ULayer, ZLayer, ZIO}
/** Representa un bloque dentro de la cadena */
final case class Block(index: Int, entry: LedgerEntry, contract: Option[String])

/** Interfaz para la generación y validación de contratos inteligentes */
trait SmartContract {
  def generate(entry: LedgerEntry): String
  def validate(contract: String, entry: LedgerEntry): Boolean
}

/** Ledger que registra cada operación en una cadena de bloques */
trait BlockchainLedger extends Ledger {
  def getBlocks: UIO[List[Block]]
}

/** Implementación en memoria que simula una red DLT */
final class InMemoryBlockchainLedger(
    entries: Ref[List[LedgerEntry]],
    blocks: Ref[List[Block]],
    contract: Option[SmartContract]
) extends BlockchainLedger {

  private def append(entry: LedgerEntry): UIO[Unit] =
    for {
      blks  <- blocks.get
      c      = contract.map(_.generate(entry))
      _     <- ZIO.when(c.isDefined)(
                 ZIO.succeed(contract.get.validate(c.get, entry))
               )
      _     <- blocks.update(_ :+ Block(blks.length + 1, entry, c))
      _     <- entries.update(_ :+ entry)
    } yield ()

  override def recordAsset(asset: Asset): UIO[Unit] =
    append(AssetEntry(asset))

  override def recordLiability(liability: Liability): UIO[Unit] =
    append(LiabilityEntry(liability))

  override def recordInvestment(investment: Investment): UIO[Unit] =
    append(InvestmentEntry(investment))

  override def getHistory: UIO[List[LedgerEntry]] = entries.get

  override def updateEntry(id: String, value: String): UIO[Unit] =
    entries.update(_.map {
      case AssetEntry(a: DataAsset) if a.id == id            =>
        AssetEntry(a.copy(data = value))
      case LiabilityEntry(l: EthicalLiability) if l.id == id =>
        LiabilityEntry(l.copy(description = value))
      case InvestmentEntry(i: BasicInvestment) if i.id == id =>
        InvestmentEntry(i.copy(quantity = BigDecimal(value)))
      case other                                             => other
    })

  override def deleteEntry(id: String): UIO[Unit] =
    entries.update(_.filterNot(_.id == id)) *> blocks.update(_.filterNot(_.entry.id == id))

  override def getBlocks: UIO[List[Block]] = blocks.get
}

object InMemoryBlockchainLedger {
  def layer(contract: Option[SmartContract] = None): ULayer[BlockchainLedger] =
    ZLayer {
      for {
        entries <- Ref.make(List.empty[LedgerEntry])
        blocks  <- Ref.make(List.empty[Block])
      } yield new InMemoryBlockchainLedger(entries, blocks, contract)
    }
}
