# Entystal

Esqueleto de proyecto Scala para aplicaciones de trazabilidad, balance y certificación ética.

## Estructura

Revisa la carpeta `core/` para el módulo principal.

## Uso

Necesitas tener Java (JDK) y [sbt](https://www.scala-sbt.org/) instalados.
Si no cuentas con sbt, revisa [docs/setup-sbt.md](docs/setup-sbt.md).

```bash
sbt scalafmtAll   # Formateo de código
sbt test          # Ejecutar pruebas
```

Ejemplo de registro por CLI:

```bash
sbt "core/run --mode asset --assetId id-101 --assetDesc 'Datos relevantes CLI'"
```

Antes, configura PostgreSQL con `core/sql/entystal_schema.sql` si vas a usar `SqlLedger`.

## Hook de Git

Para habilitar el hook de pre-commit:

```bash
ln -s ../../scripts/pre-commit .git/hooks/pre-commit
```

## Integración continua

Este repositorio cuenta con un flujo de GitHub Actions que ejecuta `sbt scalafmtAll` y `sbt test` en cada push.
