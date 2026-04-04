#!/usr/bin/env bash
# Copyright (c) 2026 The T1taniumF0rge Industries® (Inc.)
set -euo pipefail

./scripts/run-gradle.sh --no-daemon clean build
