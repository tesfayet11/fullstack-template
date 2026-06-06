#!/usr/bin/env bash
set -euo pipefail

if [ $# -lt 1 ]; then
  echo "Usage: ./scripts/init-project.sh <project-name> [package-name]"
  echo "Example: ./scripts/init-project.sh my-saas-app com.mycompany.mysaasapp"
  exit 1
fi

PROJECT_NAME="$1"
PACKAGE_NAME="${2:-}"

if [ -z "$PACKAGE_NAME" ]; then
  SANITIZED=$(echo "$PROJECT_NAME" | tr '[:upper:]' '[:lower:]' | sed 's/[^a-z0-9]//g')
  PACKAGE_NAME="com.${SANITIZED}.app"
fi

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
OLD_PACKAGE="com.template.app"
OLD_PACKAGE_PATH="com/template/app"
NEW_PACKAGE_PATH="$(echo "$PACKAGE_NAME" | tr '.' '/')"

echo "Initializing project:"
echo "  PROJECT_NAME:  $PROJECT_NAME"
echo "  PACKAGE_NAME:  $PACKAGE_NAME"
echo "  DB_NAME:       ${PROJECT_NAME}_db"

replace_in_files() {
  local search="$1"
  local replace="$2"
  local files
  files=$(grep -rl "$search" "$ROOT_DIR" \
    --exclude-dir=node_modules \
    --exclude-dir=target \
    --exclude-dir=.git \
    --exclude="init-project.sh" 2>/dev/null || true)
  if [ -z "$files" ]; then
    return 0
  fi
  while IFS= read -r file; do
    [ -n "$file" ] && sed -i "s|${search}|${replace}|g" "$file"
  done <<< "$files"
}

replace_in_files "{{PROJECT_NAME}}" "$PROJECT_NAME"
replace_in_files "{{PACKAGE_NAME}}" "$PACKAGE_NAME"
replace_in_files "$OLD_PACKAGE" "$PACKAGE_NAME"

JAVA_SRC="$ROOT_DIR/backend/src/main/java"
JAVA_TEST="$ROOT_DIR/backend/src/test/java"

if [ "$OLD_PACKAGE_PATH" != "$NEW_PACKAGE_PATH" ] && [ -d "$JAVA_SRC/$OLD_PACKAGE_PATH" ]; then
  mkdir -p "$JAVA_SRC/$(dirname "$NEW_PACKAGE_PATH")"
  mv "$JAVA_SRC/$OLD_PACKAGE_PATH" "$JAVA_SRC/$NEW_PACKAGE_PATH"
fi

if [ "$OLD_PACKAGE_PATH" != "$NEW_PACKAGE_PATH" ] && [ -d "$JAVA_TEST/$OLD_PACKAGE_PATH" ]; then
  mkdir -p "$JAVA_TEST/$(dirname "$NEW_PACKAGE_PATH")"
  mv "$JAVA_TEST/$OLD_PACKAGE_PATH" "$JAVA_TEST/$NEW_PACKAGE_PATH"
fi

if [ -f "$ROOT_DIR/.env.example" ]; then
  cp "$ROOT_DIR/.env.example" "$ROOT_DIR/.env"
fi

if [ -f "$ROOT_DIR/frontend/.env.example" ]; then
  cp "$ROOT_DIR/frontend/.env.example" "$ROOT_DIR/frontend/.env.local"
fi

MAIN_CLASS_DIR="$JAVA_SRC/$NEW_PACKAGE_PATH"
if [ -d "$MAIN_CLASS_DIR" ]; then
  for old_file in "$MAIN_CLASS_DIR"/TemplateBackendApplication*.java; do
    [ -f "$old_file" ] || continue
    new_file="$MAIN_CLASS_DIR/$(echo "$PROJECT_NAME" | sed 's/-//g' | sed 's/^./\U&/')Application.java"
    if [ "$old_file" != "$new_file" ]; then
      sed -i "s/TemplateBackendApplication/$(basename "$new_file" .java)/g" "$old_file"
      mv "$old_file" "$new_file"
    fi
  done
fi

echo ""
echo "Project initialized successfully."
echo ""
echo "Next steps:"
echo "  1. docker compose up -d"
echo "  2. cd backend && ./mvnw spring-boot:run"
echo "  3. cd frontend && npm install && npm run dev"
echo "  4. Open http://localhost:3000"
