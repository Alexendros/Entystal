#!/usr/bin/env bash
# Ejecuta pruebas de carga simples contra el API REST.
# Guarda el resultado en docs/performance/base-<fecha>.md
set -euo pipefail

URL=${1:-http://localhost:8080}
OUTPUT_DIR="docs/performance"
DATE=$(date +%Y-%m-%d)
OUT_FILE="$OUTPUT_DIR/base-$DATE.md"
mkdir -p "$OUTPUT_DIR"

if ! command -v wrk >/dev/null 2>&1; then
  echo "wrk no encontrado, instalándolo" >&2
  if command -v apt-get >/dev/null 2>&1; then
    sudo apt-get update && sudo apt-get install -y wrk
  else
    echo "No se pudo instalar wrk automáticamente" >&2
    exit 1
  fi
fi

# Iniciar el servidor REST en segundo plano
sbt "rest/run" &
SERVER_PID=$!
trap 'kill $SERVER_PID' EXIT

# Esperar a que el servidor esté listo
for i in {1..45}; do
  if curl -s "$URL/historial" >/dev/null; then
    break
  fi
  sleep 2
done
if ! curl -s "$URL/historial" >/dev/null; then
  echo "El servidor no respondió a tiempo" >&2
  exit 1
fi

# Crear script temporal para la petición POST
POST_SCRIPT=$(mktemp)
cat > "$POST_SCRIPT" <<'LUA'
wrk.method = "POST"
wrk.body   = '{"tipo":"activo","identificador":"bench","descripcion":"ok"}'
wrk.headers["Content-Type"] = "application/json"
LUA

{
  echo "# Resultados de benchmark $DATE"
  echo "## POST /registro"
  wrk -t2 -c10 -d10s -s "$POST_SCRIPT" "$URL/registro"
  echo "## GET /historial"
  wrk -t2 -c10 -d10s "$URL/historial"
} | tee "$OUT_FILE"

rm "$POST_SCRIPT"
wait $SERVER_PID
