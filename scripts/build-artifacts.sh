#!/usr/bin/env bash
# Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
set -euo pipefail

loader="${1:-}"
if [[ -z "$loader" ]]; then
  echo "Usage: $0 <fabric|forge|neoforge|quilt>" >&2
  exit 1
fi

case "$loader" in
  fabric)
    ;;
  forge|neoforge|quilt)
    echo "$loader build is currently disabled; keeping the entrypoint for future loader ports."
    exit 0
    ;;
  *)
    echo "Unsupported loader: $loader" >&2
    exit 1
    ;;
esac

./scripts/run-gradle.sh --no-daemon build

mkdir -p "$loader/build/libs"
jar_source="$(find build/libs -maxdepth 1 -type f -name '*-remapped.jar' | sort | head -n 1)"
if [[ -z "$jar_source" ]]; then
  jar_source="$(find build/libs -maxdepth 1 -type f -name '*.jar' \
    ! -name '*-sources.jar' \
    ! -name '*-javadoc.jar' \
    | sort | head -n 1)"
fi

if [[ -z "$jar_source" ]]; then
  echo "No built JAR found in build/libs" >&2
  exit 1
fi

cp "$jar_source" "$loader/build/libs/player-death-animation-${loader}.jar"
