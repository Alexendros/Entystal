package entystal.viewmodel

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import entystal.ledger.InMemoryLedger
import entystal.service.{RegistroService, TestNotifier}
import entystal.viewmodel.RegistroValidator
import zio.{Runtime, ZIO}

class RegistroViewModelSpec extends AnyFlatSpec with Matchers {
  implicit val runtime: Runtime[Any] = Runtime.default

  private def newVm(notifier: TestNotifier) = {
    val ledger = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(ZIO.scoped(InMemoryLedger.live.build.map(_.get))).getOrThrow()
    }
    val service = new RegistroService(ledger)
    new RegistroViewModel(service, notifier, new RegistroValidator)
  }

  "RegistroViewModel" should "notificar error si falta ID" in {
    val notifier = new TestNotifier
    val vm       = newVm(notifier)
    vm.descripcion.value = "d"
    vm.registrar()
    notifier.last shouldBe Some("error" -> "ID requerido")
  }

  it should "notificar error si la cantidad no es numérica" in {
    val notifier = new TestNotifier
    val vm       = newVm(notifier)
    vm.tipo.value = "inversion"
    vm.identificador.value = "inv1"
    vm.descripcion.value = "abc"
    vm.registrar()
    notifier.last shouldBe Some("error" -> "La cantidad debe ser numérica")
  }

  it should "notificar éxito al registrar un activo" in {
    val notifier = new TestNotifier
    val vm       = newVm(notifier)
    vm.tipo.value = "activo"
    vm.identificador.value = "a1"
    vm.descripcion.value = "desc"
    vm.registrar()
    notifier.last shouldBe Some("success" -> "Registro completado")
  }
}
