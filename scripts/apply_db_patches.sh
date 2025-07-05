#!/usr/bin/env bash
# Aplica los parches de base de datos
# Uso: scripts/apply_db_patches.sh <conexion>
set -euo pipefail
CONN=${1:-"-h localhost -d entystal"}
psql $CONN -f core/sql/entystal_schema.sql
psql $CONN -f core/sql/fix-CVE-2024-4317.sql
