#!/bin/bash
set -euo pipefail

: "${KEYCLOAK_REALM_NAME:?KEYCLOAK_REALM_NAME must be set}"
: "${DEV_USER_EMAIL:?DEV_USER_EMAIL must be set}"
: "${DEV_USER_PASSWORD:?DEV_USER_PASSWORD must be set}"

TEMPLATE_FILE="/opt/keycloak/data/import-template/thevideoclub.realm.json"
IMPORT_DIR="/opt/keycloak/data/import"
OUTPUT_FILE="$IMPORT_DIR/thevideoclub.realm.json"

if [[ ! -f "$TEMPLATE_FILE" ]]; then
  echo "ERROR: realm template not found at $TEMPLATE_FILE" >&2
  exit 1
fi

mkdir -p "$IMPORT_DIR"

escape_for_sed() {
  printf '%s' "$1" | sed -e 's/[&\/|]/\\&/g'
}

escaped_realm=$(escape_for_sed "$KEYCLOAK_REALM_NAME")
escaped_email=$(escape_for_sed "$DEV_USER_EMAIL")
escaped_password=$(escape_for_sed "$DEV_USER_PASSWORD")

sed -e "s|__KEYCLOAK_REALM_NAME__|$escaped_realm|g" \
    -e "s|__DEV_USER_EMAIL__|$escaped_email|g" \
    -e "s|__DEV_USER_PASSWORD__|$escaped_password|g" \
    "$TEMPLATE_FILE" > "$OUTPUT_FILE"

echo "Generated realm config at $OUTPUT_FILE"

# Start Keycloak
exec /opt/keycloak/bin/kc.sh start-dev --import-realm
