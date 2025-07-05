package entystal.gui

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import java.util.prefs.Preferences

class ThemeManagerSpec extends AnyFlatSpec with Matchers {
  private val prefs = Preferences.userRoot().node("/entystal/gui")

  "ThemeManager" should "cargar claro por defecto" in {
    prefs.remove("theme")
    ThemeManager.loadTheme() shouldBe ThemeManager.Light
  }

  it should "leer tema oscuro" in {
    prefs.put("theme", "dark")
    ThemeManager.loadTheme() shouldBe ThemeManager.Dark
  }
}
