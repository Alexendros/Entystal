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

## Configuración de PostgreSQL

1. Instala PostgreSQL con tu gestor de paquetes favorito, por ejemplo:
   ```bash
   sudo apt-get install postgresql
   ```
   Puedes consultar otras opciones en la [documentación oficial](https://www.postgresql.org/download/).
2. Crea un usuario y base de datos para la aplicación:
   ```bash
   sudo -u postgres createuser --pwprompt entystal_user
   sudo -u postgres createdb -O entystal_user entystal
   ```
3. Aplica el esquema ético ejecutando:
   ```bash
   psql -U entystal_user -d entystal -f core/sql/entystal_schema.sql
   ```
4. Ajusta las credenciales en tu código o variables de entorno. Por ejemplo,
   en `core/src/test/scala/entystal/ledger/SqlLedgerSpec.scala` se configura el
   `Transactor` de Doobie así:
   ```scala
   Transactor.fromDriverManager[Task](
     "org.postgresql.Driver",
     "jdbc:postgresql://localhost:5432/entystal",
     "entystal_user",
     "tu_clave"
   )
   ```
   Sustituye `entystal_user` y `tu_clave` por tus valores reales.

## Pruebas de integración

Las pruebas que ejercitan `SqlLedger` leen las credenciales de la base de datos
desde las variables de entorno `PGUSER` y `PGPASSWORD`. Configúralas antes de
ejecutar:

```bash
sbt test
```

## Contribución

Para detalles sobre cómo enviar cambios sin conflictos revisa [CONTRIBUTING.md](CONTRIBUTING.md).