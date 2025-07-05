package entystal.util

/** Utilidades sencillas para cumplir con GDPR y proteger datos personales. */
object PrivacyUtils {
  private val emailRegex = "(?i)[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}".r
  private val phoneRegex = "\\b\\d{7,15}\\b".r

  /** Reemplaza datos personales detectados por la marca [REDACTED]. */
  def sanitize(text: String): String =
    phoneRegex.replaceAllIn(emailRegex.replaceAllIn(text, "[REDACTED]"), "[REDACTED]")
}
