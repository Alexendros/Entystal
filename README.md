# Entystal

Esqueleto de proyecto Scala para aplicaciones de trazabilidad, balance y certificación ética.

## Estructura

Revisa la carpeta `core/` para el módulo principal.

## Uso

Requiere [sbt](https://www.scala-sbt.org/) instalado.
Si no lo tienes, ejecuta `scripts/install_sbt.sh`. Este script intenta usar `apt`
y, si no está disponible, descarga `sbt` directamente desde GitHub para dejarlo
listo en tu sistema.

```bash
sbt scalafmtAll   # Formateo de código
sbt test          # Ejecutar pruebas
```

Ejemplo de registro por CLI:

```bash
sbt "core/run --mode asset --assetId id-101 --assetDesc 'Datos relevantes CLI'"
```

La salida debería mostrar algo similar a:

```
Registrado activo: DataAsset(id-101, Datos relevantes CLI, 172xxxxxxx, 1)
```

Antes, configura PostgreSQL con `core/sql/entystal_schema.sql` si vas a usar `SqlLedger`.

## Pruebas de integración

Las pruebas que ejercitan `SqlLedger` leen las credenciales de la base de datos
desde las variables de entorno `PGUSER` y `PGPASSWORD`. Configúralas antes de
ejecutar:

```bash
sbt test
```

Estas pruebas se ejecutan solo si PostgreSQL está activo y accesible en
`localhost:5432`. Si no es así se marcan como **ignoradas** automáticamente.

## Contribución

Para detalles sobre cómo enviar cambios sin conflictos revisa [CONTRIBUTING.md](CONTRIBUTING.md).