#!/bin/bash
set -euo pipefail

: "${KEYCLOAK_REALM_NAME:?KEYCLOAK_REALM_NAME must be set}"

TEMPLATE_FILE="/opt/keycloak/data/import/thevideoclub.realm.json.tpl"
OUTPUT_FILE="/opt/keycloak/data/import/thevideoclub.realm.json"

# Ensure the import directory exists and is writable
mkdir -p /opt/keycloak/data/import

# Replace the placeholder in the template with the realm name
if [[ -f "$TEMPLATE_FILE" ]]; then
  realm_escaped=$(printf '%s' "$KEYCLOAK_REALM_NAME" | sed 's/[&\/|]/\\&/g')
  sed "s|__KEYCLOAK_REALM_NAME__|$realm_escaped|g" "$TEMPLATE_FILE" > "$OUTPUT_FILE"
  echo "Generated realm config at $OUTPUT_FILE with KEYCLOAK_REALM_NAME=${KEYCLOAK_REALM_NAME}"
fi

# Start Keycloak
exec /opt/keycloak/bin/kc.sh start-dev --import-realm
