# Entystal

Esqueleto de proyecto Scala para aplicaciones de trazabilidad, balance y certificación ética.

## Estructura

Revisa la carpeta `core/` para el módulo principal.

## Uso

Requiere [sbt](https://www.scala-sbt.org/) instalado. Si no lo tienes,
puedes ejecutar el script `scripts/install_sbt.sh` (requiere permisos de
superusuario y acceso a internet).

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

## Contribución

Para detalles sobre cómo enviar cambios sin conflictos revisa [CONTRIBUTING.md](CONTRIBUTING.md).