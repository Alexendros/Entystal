# Instalación de sbt

Para compilar y ejecutar este proyecto necesitas **Java (JDK)** y [sbt](https://www.scala-sbt.org/).

## Linux

En sistemas Debian o Ubuntu puedes ejecutar el script `scripts/install_sbt.sh` incluido en este repositorio:

```bash
bash scripts/install_sbt.sh
```

También puedes seguir los pasos manuales:

1. Importa la clave GPG del repositorio oficial de sbt.
2. Añade el repositorio a tus fuentes de paquetes.
3. Actualiza e instala sbt con `apt`.

## macOS

Si utilizas [Homebrew](https://brew.sh/):

```bash
brew install sbt
```

## Windows

Descarga el instalador desde la [página oficial de sbt](https://www.scala-sbt.org/download.html) y ejecútalo.

Tras la instalación, comprueba la versión con:

```bash
sbt --version
```
