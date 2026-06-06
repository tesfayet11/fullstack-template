#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

if [ -z "${JAVA_HOME:-}" ] || [ ! -x "${JAVA_HOME}/bin/javac" ]; then
  for candidate in \
    /usr/lib/jvm/java-25-openjdk-amd64 \
    /usr/lib/jvm/java-21-openjdk-amd64 \
    /usr/lib/jvm/default-java; do
    if [ -x "$candidate/bin/javac" ]; then
      export JAVA_HOME="$candidate"
      break
    fi
  done
fi

if [ -z "${JAVA_HOME:-}" ] || [ ! -x "${JAVA_HOME}/bin/javac" ]; then
  echo "Error: JAVA_HOME must point to a JDK (with javac)."
  echo "Example: export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64"
  exit 1
fi

export PATH="$JAVA_HOME/bin:$PATH"

echo "Using JAVA_HOME=$JAVA_HOME"
java -version

cd "$ROOT_DIR"
set -a
[ -f .env ] && source .env
set +a

cd "$ROOT_DIR/backend"
exec ./mvnw spring-boot:run "$@"
