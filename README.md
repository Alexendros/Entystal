# Entystal

Sistema funcional en Scala para trazabilidad ética y registro auditable de activos, pasivos e inversiones.

## Estructura

El código principal se encuentra en la carpeta `core/` y está organizado en módulos de modelo, ledger y vistas (CLI y GUI).

## Funcionalidades principales

- Ledger en memoria y opción de persistencia en PostgreSQL.
- Interfaz de línea de comandos (CLI) para registrar eventos contables.
- Interfaz gráfica (GUI) básica con ScalaFX para registrar activos, pasivos e inversiones.
- Generación de JAR ejecutable mediante `sbt-assembly`.
- Nuevo módulo `rest` con API HTTP para registrar eventos y consultar historial.

## Accesibilidad

La GUI ahora define texto accesible (`accessibleText`) en cada botón y campo,
atajos de teclado mediante `mnemonicParsing` para las acciones principales y un
orden de tabulación lógico para navegar sólo con el teclado.

## Requisitos

- Java JDK 8 o superior.
- [sbt](https://www.scala-sbt.org/). Si no lo tienes, ejecuta `scripts/install_sbt.sh` para instalarlo automáticamente.
- PostgreSQL \>= 17.3 con los parches de seguridad **CVE-2024-10979** y **CVE-2024-4317** aplicados.

## Instalación

1. Clona el repositorio:
   ```bash
   git clone https://github.com/Alexendros/Entystal.git
   cd Entystal
   ```
2. Instala sbt (si aún no lo tienes):
   ```bash
   bash scripts/install_sbt.sh
   ```
3. Inicializa la base de datos y aplica los parches:
   ```bash
   bash scripts/apply_db_patches.sh
   ```
4. Compila y formatea el proyecto:
   ```bash
   sbt scalafmtAll compile
   ```
5. Ejecuta las pruebas unitarias:
   ```bash
   sbt test
   ```
6. (Opcional) Genera un JAR ensamblado para distribuir la GUI:
   ```bash
   sbt assembly
   # El archivo quedará en target/scala-2.13/*-assembly.jar
   ```

## Uso de la aplicación

### Por CLI

```bash
sbt 'core/run --mode asset --assetId id-101 --assetDesc "Datos relevantes CLI"'
```
En Windows (incluido PowerShell) funciona igual usando comillas simples para
envolver todo el comando:
```cmd
sbt 'core/run --mode asset --assetId id-101 --assetDesc "Datos relevantes CLI"'
```
Si prefieres evitar problemas de escape, puedes usar el script de ayuda:
```bash
./scripts/run-cli.sh asset id-101 "Datos relevantes CLI"
```
o en PowerShell:
```powershell
./scripts/run-cli.ps1 -Mode asset -AssetId id-101 -AssetDesc "Datos relevantes CLI"
```
La salida será similar a:
```text
Registrado activo: DataAsset(id-101, Datos relevantes CLI, 172xxxxxxx, 1)
```

### Por GUI

Lanza la interfaz gráfica directamente con sbt:
```bash
sbt "core/runMain entystal.gui.GuiApp"
```
Si generaste el JAR ensamblado también puedes ejecutarlo con:
```bash
java -jar target/scala-2.13/entystal-core-assembly-*.jar
```

#### Cambio de tema

En la pestaña **Registro** hay un botón *Cambiar tema*. Al pulsarlo se alterna
entre modo claro y oscuro y la aplicación recordará tu preferencia.

Antes de utilizar `SqlLedger` ejecuta `scripts/apply_db_patches.sh` para
inicializar el esquema y aplicar las correcciones de seguridad.

## Pruebas de integración

Las pruebas que usan `SqlLedger` requieren las variables de entorno `PGUSER` y `PGPASSWORD`. En los workflows de CI estas credenciales se configuran como **GitHub Secrets** y se inyectan de forma segura. Si ejecutas las pruebas localmente define esas variables en tu terminal o en un archivo `.env`.
```bash
sbt test
```
Si la base de datos no est\u00e1 disponible en `localhost:5432` las pruebas se marcan como
**ignoradas**.

## Cobertura de código

Genera el informe de cobertura con:
```bash
sbt coverage test coverageAggregate
```
El reporte HTML quedará en `target/scala-*/scoverage-report/index.html`.


## Traducciones

Las cadenas de texto de la GUI se almacenan en `core/src/main/resources/i18n`.
Para añadir un nuevo idioma crea un archivo `messages_<código>.properties` con
las claves existentes traducidas. Luego invoca `I18n.setLocale` con el locale
correspondiente (por ejemplo `Locale.FRENCH`). Los idiomas soportados se
definen en `I18n.supportedLocales`.


## Contribución

Para detalles sobre cómo enviar cambios sin conflictos revisa [CONTRIBUTING.md](CONTRIBUTING.md).

## Windows y macOS

Tras instalar Java y sbt, los comandos de este README funcionan igual en ambos sistemas.
Puedes ejecutar la aplicación con:

```bash
sbt run
```

o lanzar la GUI directamente:

```bash
sbt "core/runMain entystal.gui.GuiApp"
```

Gracias a la variable `javafxPlatform` definida en `build.sbt` las librerías de OpenJFX se seleccionan automáticamente para tu sistema operativo.
