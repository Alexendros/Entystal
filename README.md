# Entystal

Sistema funcional en Scala para trazabilidad ética y registro auditable de activos, pasivos e inversiones.

## Estructura

El código principal se encuentra en la carpeta `core/` y está organizado en módulos de modelo, ledger y vistas (CLI y GUI).

## Funcionalidades principales

- Ledger en memoria y opción de persistencia en PostgreSQL.
- Interfaz de línea de comandos (CLI) para registrar eventos contables.
- Interfaz gráfica (GUI) básica con ScalaFX para registrar activos, pasivos e inversiones.
- Generación de JAR ejecutable mediante `sbt-assembly`.
- Exportación de eventos a JSON.
- Panel de métricas tipo dashboard.

## Requisitos

- Java JDK 8 o superior.
- [sbt](https://www.scala-sbt.org/). Si no lo tienes, ejecuta `scripts/install_sbt.sh` para instalarlo automáticamente.

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
3. Compila y formatea el proyecto:
   ```bash
   sbt scalafmtAll compile
   ```
4. Ejecuta las pruebas unitarias:
   ```bash
   sbt test
   ```
5. (Opcional) Genera un JAR ensamblado para distribuir la GUI:
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

#### Nuevas funcionalidades de la GUI

- **Cambio de idioma**: abre el menú `View` y selecciona `Language` para alternar entre español e inglés sin reiniciar.
- **Exportación JSON**: desde `Archivo > Exportar...` guarda el historial en la carpeta `exports/`.
- **Dashboard**: ejecuta `sbt "core/runMain entystal.gui.DashboardApp"` o usa el botón *Dashboard* en la ventana principal.

Antes de utilizar `SqlLedger` recuerda aplicar el script `core/sql/entystal_schema.sql` en tu instancia de PostgreSQL.

## Pruebas de integración

Las pruebas que usan `SqlLedger` necesitan las variables `PGUSER` y `PGPASSWORD` configuradas. Se ejecutan automáticamente con:
```bash
sbt test
```
Si la base de datos no está disponible en `localhost:5432` las pruebas se marcan como **ignoradas**.

## Contribución

Para detalles sobre cómo enviar cambios sin conflictos revisa [CONTRIBUTING.md](CONTRIBUTING.md).

## Desarrollo

- Ejecuta `sbt scalafmtAll` para aplicar el formato de código.
- Ejecuta `sbt test` para correr todas las pruebas.
- El flujo de CI en `.github/workflows/ci.yml` verifica formato y tests en cada PR.
- Necesitas Java 17, PostgreSQL 15 y sbt 1.9.9 instalados.
\n
