#!/usr/bin/env sh
# Minimal Gradle launcher for Remapper project
if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
fi

if [ -f "$HOME/.gradle/wrapper/dists/gradle-8.7-bin/*/gradle-8.7/bin/gradle" ]; then
    exec "$HOME/.gradle/wrapper/dists/gradle-8.7-bin"/*/gradle-8.7/bin/gradle "$@"
fi

echo "Gradle is required to build Remapper. Please install Gradle or open in Android Studio."
exit 1
