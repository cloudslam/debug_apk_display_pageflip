#!/usr/bin/env sh
set -e

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

echo "Gradle is required to build this project because binary wrapper files are intentionally not checked in." >&2
echo "Install Gradle, then run: ./gradlew assembleDebug" >&2
exit 1
