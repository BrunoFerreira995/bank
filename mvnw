#!/bin/sh
set -eu

BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
MVN_VERSION=3.9.10
MVN_DIR="$BASE_DIR/.mvn/wrapper/apache-maven-$MVN_VERSION"
MVN_ZIP="$BASE_DIR/.mvn/wrapper/apache-maven-$MVN_VERSION-bin.zip"
MVN_URL="https://archive.apache.org/dist/maven/maven-3/$MVN_VERSION/binaries/apache-maven-$MVN_VERSION-bin.zip"

if [ ! -x "$MVN_DIR/bin/mvn" ]; then
  mkdir -p "$BASE_DIR/.mvn/wrapper"
  if [ ! -f "$MVN_ZIP" ]; then
    if command -v curl >/dev/null 2>&1; then
      curl -fsSL "$MVN_URL" -o "$MVN_ZIP"
    elif command -v wget >/dev/null 2>&1; then
      wget -q "$MVN_URL" -O "$MVN_ZIP"
    else
      echo "curl or wget is required to download Maven $MVN_VERSION" >&2
      exit 1
    fi
  fi
  unzip -q "$MVN_ZIP" -d "$BASE_DIR/.mvn/wrapper"
fi

exec "$MVN_DIR/bin/mvn" "$@"
