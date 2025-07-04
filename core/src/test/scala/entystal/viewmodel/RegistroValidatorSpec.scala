package entystal.viewmodel

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RegistroValidatorSpec extends AnyFlatSpec with Matchers {
  private val validator = new RegistroValidator

  "RegistroValidator" should "aceptar datos validos" in {
    val data = RegistroData("activo", "a1", "desc")
    validator.validate(data) shouldBe Right(())
  }

  it should "requerir id" in {
    val data = RegistroData("activo", "", "desc")
    validator.validate(data) shouldBe Left("ID requerido")
  }

  it should "requerir descripcion" in {
    val data = RegistroData("activo", "a1", "")
    validator.validate(data) shouldBe Left("Descripción requerida")
  }

  it should "validar numeros en inversion" in {
    val data = RegistroData("inversion", "i1", "abc")
    validator.validate(data) shouldBe Left("La cantidad debe ser numérica")
  }

  it should "aceptar inversion con cantidad" in {
    val data = RegistroData("inversion", "i1", "10")
    validator.validate(data) shouldBe Right(())
  }
}
