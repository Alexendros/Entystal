package entystal

import zio.test.{ZIOSpecDefault, assertTrue, Spec, TestEnvironment}
import zio.test._
import java.util.Locale
import java.util.ResourceBundle
import entystal.i18n.I18n

object I18nSpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment, Any] =
    suite("I18nSpec")(
      test("carga recursos en español") {
        val rb = ResourceBundle.getBundle("i18n/messages", Locale.forLanguageTag("es"))
        assertTrue(rb.getString("button.registrar") == "Registrar")
      },
      test("carga recursos en inglés") {
        val rb = ResourceBundle.getBundle("i18n/messages", Locale.forLanguageTag("en"))
        assertTrue(rb.getString("button.registrar") == "Register")
      },
      test("carga recursos en francés") {
        val rb = ResourceBundle.getBundle("i18n/messages", Locale.FRENCH)
        assertTrue(rb.getString("button.registrar") == "Enregistrer")
      },
      test("I18n soporta francés") {
        assertTrue(I18n.supportedLocales.contains(Locale.FRENCH))
      }
    )
}
