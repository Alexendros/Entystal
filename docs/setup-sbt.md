# Instalación de sbt

Para compilar y ejecutar este proyecto necesitas **Java (JDK)** y [sbt](https://www.scala-sbt.org/).

## Linux

En sistemas Debian o Ubuntu puedes ejecutar el script `scripts/install_sbt.sh` incluido en este repositorio:

```bash
bash scripts/install_sbt.sh
```

El script detecta si `apt` puede instalar sbt directamente y, de no ser así, configura el repositorio oficial automáticamente.

También puedes seguir los pasos manuales:

1. Importa la clave GPG y guarda el keyring:
   ```bash
   curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99E82A75642AC823" \
     | gpg --dearmor | sudo tee /usr/share/keyrings/sbt-keyring.gpg > /dev/null
   ```
2. Añade el repositorio a tus fuentes de paquetes:
   ```bash
   echo "deb [signed-by=/usr/share/keyrings/sbt-keyring.gpg] https://repo.scala-sbt.org/scalasbt/debian all main" \
     | sudo tee /etc/apt/sources.list.d/sbt.list
   ```
3. Actualiza e instala sbt con `apt`:
   ```bash
   sudo apt-get update && sudo apt-get install -y sbt
   ```

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

Recuerda usar PostgreSQL \>= 17.3 y aplicar los parches **CVE-2024-10979** y
**CVE-2024-4317** antes de ejecutar el proyecto.
