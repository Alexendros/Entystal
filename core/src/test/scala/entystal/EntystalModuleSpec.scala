package entystal

import zio.test.{ZIOSpecDefault, assertTrue, Spec, TestEnvironment}
import zio.test._
import zio.Scope

object EntystalModuleSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("EntystalModuleSpec")(
      test("la capa proporciona un ledger vac\u00edo") {
        for {
          ledger  <- EntystalModule.layer.build.map(_.get)
          history <- ledger.getHistory
        } yield assertTrue(history.isEmpty)
      }
    )
}
