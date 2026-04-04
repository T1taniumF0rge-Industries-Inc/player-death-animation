#!/usr/bin/env bash
# Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
set -euo pipefail

GRADLE_VERSION="8.7"
DIST_DIR=".gradle-dist"
GRADLE_HOME="$DIST_DIR/gradle-$GRADLE_VERSION"
GRADLE_BIN="$GRADLE_HOME/bin/gradle"

if [[ ! -x "$GRADLE_BIN" ]]; then
  mkdir -p "$DIST_DIR"
  ARCHIVE="$DIST_DIR/gradle-$GRADLE_VERSION-bin.zip"
  if [[ ! -f "$ARCHIVE" ]]; then
    curl -fsSL "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -o "$ARCHIVE"
  fi
  unzip -q -o "$ARCHIVE" -d "$DIST_DIR"
fi

"$GRADLE_BIN" "$@"
