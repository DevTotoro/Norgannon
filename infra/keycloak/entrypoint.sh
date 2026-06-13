#!/bin/bash
set -euo pipefail

: "${KEYCLOAK_REALM_NAME:?KEYCLOAK_REALM_NAME must be set}"

TEMPLATE_FILE="/opt/keycloak/data/import-template/thevideoclub.realm.json.tpl"
IMPORT_DIR="/opt/keycloak/data/import"
OUTPUT_FILE="$IMPORT_DIR/thevideoclub.realm.json"

if [[ ! -f "$TEMPLATE_FILE" ]]; then
  echo "ERROR: realm template not found at $TEMPLATE_FILE" >&2
  exit 1
fi

mkdir -p "$IMPORT_DIR"


realm_escaped=$(printf '%s' "$KEYCLOAK_REALM_NAME" | sed 's/[&\/|]/\\&/g')
sed "s|__KEYCLOAK_REALM_NAME__|$realm_escaped|g" "$TEMPLATE_FILE" > "$OUTPUT_FILE"
echo "Generated realm config at $OUTPUT_FILE with KEYCLOAK_REALM_NAME=${KEYCLOAK_REALM_NAME}"

# Start Keycloak
exec /opt/keycloak/bin/kc.sh start-dev --import-realm
