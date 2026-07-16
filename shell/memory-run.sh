#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

usage() {
    cat <<EOF
Usage: bash shell/memory-run.sh [--installed-default] [--port=7080|7080]

Environment:
  ZRLOG_MEMORY_PORT  Default port when no argument is provided.
EOF
}

PORT="${ZRLOG_MEMORY_PORT:-7080}"
MODE="review-fixture"
for arg in "$@"; do
    case "$arg" in
        --help|-h)
            usage
            exit 0
            ;;
        --installed-default)
            MODE="installed-default"
            ;;
        --port=*)
            PORT="${arg#--port=}"
            ;;
        [0-9]*)
            PORT="$arg"
            ;;
        *)
            usage >&2
            exit 2
            ;;
    esac
done

if ! [[ "$PORT" =~ ^[0-9]+$ ]]; then
    usage >&2
    exit 2
fi

APP_ARGS=("--port=${PORT}")
if [ "$MODE" = "installed-default" ]; then
    APP_ARGS+=("--installed-default")
fi

./mvnw -q -DskipTests install

exec ./mvnw -q -pl zrlog-blog-web exec:java \
    -Dexec.mainClass="com.zrlog.blog.MemoryApplication" \
    -Dexec.classpathScope=test \
    -Dexec.args="${APP_ARGS[*]}"
