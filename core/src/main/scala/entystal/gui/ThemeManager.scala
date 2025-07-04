package entystal.gui

import scalafx.scene.Scene
import scalafx.Includes._
import java.util.prefs.Preferences

/** Gestor de temas para la interfaz */
object ThemeManager {
  private val prefs    = Preferences.userRoot().node("/entystal/gui")
  private val ThemeKey = "theme"

  sealed trait Theme { def css: String }
  case object Light extends Theme { val css = "/css/light-theme.css" }
  case object Dark  extends Theme { val css = "/css/dark-theme.css"  }

  /** Carga el tema almacenado o usa claro por defecto */
  def loadTheme(): Theme =
    prefs.get(ThemeKey, "light") match {
      case "dark" => Dark
      case _      => Light
    }

  /** Aplica y guarda el tema actual */
  def applyTheme(scene: Scene, theme: Theme): Unit = {
    scene.stylesheets.clear()
    scene.stylesheets += theme.css
    prefs.put(
      ThemeKey,
      theme match {
        case Dark  => "dark"
        case Light => "light"
      }
    )
  }
}
