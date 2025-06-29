package entystal

import entystal.ledger.{InMemoryLedger, Ledger}
import zio.ULayer

/** Capa principal que expone el Ledger en memoria */
object EntystalModule {
  val layer: ULayer[Ledger] = InMemoryLedger.live
}
