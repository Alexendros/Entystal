package entystal.viewmodel

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import entystal.ledger.InMemoryLedger
import zio.{Runtime, ZIO}

class RegistroViewModelSpec extends AnyFlatSpec with Matchers {
  implicit val runtime: Runtime[Any] = Runtime.default

  private def newVm() = {
    val ledger = zio.Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(ZIO.scoped(InMemoryLedger.live.build.map(_.get))).getOrThrow()
    }
    new RegistroViewModel(ledger)
  }

  "RegistroViewModel" should "devolver error si falta ID" in {
    val vm = newVm()
    vm.descripcion.value = "d"
    vm.registrar() shouldBe "ID requerido"
  }

  it should "devolver error si la cantidad no es numérica" in {
    val vm = newVm()
    vm.tipo.value = "inversion"
    vm.identificador.value = "inv1"
    vm.descripcion.value = "abc"
    vm.registrar() shouldBe "La cantidad debe ser numérica"
  }

  it should "registrar un activo correctamente" in {
    val vm = newVm()
    vm.tipo.value = "activo"
    vm.identificador.value = "a1"
    vm.descripcion.value = "desc"
    vm.registrar() shouldBe "Registro completado"
  }
}
