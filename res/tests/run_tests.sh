#!/usr/bin/env bash
# Compatibility entry point for the original university test command.
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/../.."
exec bash scripts/test.sh "$@"
