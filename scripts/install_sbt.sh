#!/usr/bin/env bash
# Instalador de sbt con soporte para entornos sin apt
set -e

# Salir si ya está instalado
if command -v sbt >/dev/null 2>&1; then
  echo "sbt ya está instalado"
  exit 0
fi

if command -v apt-get >/dev/null 2>&1; then
  echo "Intentando instalar sbt con apt" >&2
  if sudo apt-get update && sudo apt-get install -y sbt; then
    echo "sbt instalado con apt"
    exit 0
  else
    echo "No se pudo instalar con apt" >&2
  fi
fi

VERSION=${SBT_VERSION:-1.9.9}
TMP=$(mktemp -d)
URL="https://github.com/sbt/sbt/releases/download/v${VERSION}/sbt-${VERSION}.tgz"
SHA_URL="${URL}.sha256"
echo "Descargando sbt ${VERSION} desde GitHub" >&2
curl -L "$URL" -o "$TMP/sbt.tgz"
OFFICIAL_SHA=$(curl -L "$SHA_URL" | cut -d ' ' -f1)
LOCAL_SHA=$(sha256sum "$TMP/sbt.tgz" | cut -d ' ' -f1)
if [ "$OFFICIAL_SHA" != "$LOCAL_SHA" ]; then
  echo "La suma SHA256 no coincide. Descarga corrupta." >&2
  exit 1
fi
tar -xzf "$TMP/sbt.tgz" -C "$TMP"

if [ "$(id -u)" -eq 0 ]; then
  install_dir=/usr/local/lib/sbt
  mv "$TMP/sbt" "$install_dir"
  ln -sf "$install_dir/bin/sbt" /usr/local/bin/sbt
else
  install_dir="$HOME/.local/sbt"
  mkdir -p "$HOME/.local/bin"
  mv "$TMP/sbt" "$install_dir"
  ln -sf "$install_dir/bin/sbt" "$HOME/.local/bin/sbt"
  export PATH="$HOME/.local/bin:$PATH"
fi
echo "sbt instalado en $install_dir"
