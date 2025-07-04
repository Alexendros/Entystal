#!/usr/bin/env bash
# Ejecuta la CLI con los argumentos dados.
# Uso: ./scripts/run-cli.sh <modo> <assetId> <"descripcion con espacios">
set -euo pipefail
MODE=${1:-asset}
ID=${2:-}
DESC=${3:-}
if [[ -z "$ID" || -z "$DESC" ]]; then
  echo "Uso: $0 <modo> <assetId> <descripcion>" >&2
  exit 1
fi
CMD="core/run --mode $MODE --assetId $ID --assetDesc \"$DESC\""
exec sbt "$CMD"

