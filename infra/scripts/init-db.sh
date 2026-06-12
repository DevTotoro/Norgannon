#!/bin/bash
set -euo pipefail

: "${POSTGRES_USER:?POSTGRES_USER must be set}"
: "${POSTGRES_PASSWORD:?POSTGRES_PASSWORD must be set}"
: "${POSTGRES_DB:?POSTGRES_DB must be set}"
: "${KEYCLOAK_DB_NAME:?KEYCLOAK_DB_NAME must be set}"

# Ensure psql uses the provided password when connecting
export PGPASSWORD="${POSTGRES_PASSWORD}"

if psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" -tAc "SELECT 1 FROM pg_database WHERE datname='${KEYCLOAK_DB_NAME}'" | grep -q 1; then
	echo "Database ${KEYCLOAK_DB_NAME} already exists"
else
	psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE DATABASE "${KEYCLOAK_DB_NAME}";
	EOSQL
fi
