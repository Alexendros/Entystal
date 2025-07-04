# entystal-core

Sistema funcional en Scala para trazabilidad ética, balance y registro auditable de recursos en proyectos colaborativos.
Incluye modelos de activos, pasivos e inversiones, ledger funcional concurrente y persistencia opcional en PostgreSQL.

## Uso rápido
1. Clona el repo y lanza `sbt compile`.
2. Configura PostgreSQL y ejecuta el script `core/sql/entystal_schema.sql`.
3. Prueba la CLI:
   `sbt "core/run --mode asset --assetId id-101 --assetDesc 'Datos relevantes CLI'"`
   En Windows, usa comillas dobles cuando el valor tenga espacios:
   `sbt "core/run --mode asset --assetId id-101 --assetDesc \"Datos relevantes CLI\""`
   La salida esperada:
   `Registrado activo: DataAsset(id-101, Datos relevantes CLI, 172xxxxxxx, 1)`
4. Ejecuta los tests:
   `sbt core/test`

## Extensiones
- API REST, control de acceso, exportación JSON, métricas Prometheus.
