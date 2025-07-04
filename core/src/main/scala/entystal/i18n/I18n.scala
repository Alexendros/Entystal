package entystal.i18n

import scalafx.beans.property.{ObjectProperty, StringProperty}
import java.util.{Locale, ResourceBundle}

/** Utilidad sencilla para manejar traducciones mediante ResourceBundle */
object I18n {
  val locale: ObjectProperty[Locale] = ObjectProperty(Locale.getDefault)

  private var bundle: ResourceBundle =
    ResourceBundle.getBundle("i18n/messages", locale.value)

  private var listeners = List.empty[() => Unit]

  /** Obtiene el texto traducido para la clave dada */
  def apply(key: String): String = bundle.getString(key)

  /** Propiedad que se actualiza cuando cambia el locale */
  def binding(key: String): StringProperty = {
    val prop = StringProperty(apply(key))
    register(() => prop.value = apply(key))
    prop
  }

  /** Cambia el locale activo y notifica a las vistas */
  def setLocale(loc: Locale): Unit = {
    locale.value = loc
    bundle = ResourceBundle.getBundle("i18n/messages", loc)
    listeners.foreach(_())
  }

  /** Permite que una vista sea notificada al cambiar el idioma */
  def register(l: () => Unit): Unit = listeners = l :: listeners
}
