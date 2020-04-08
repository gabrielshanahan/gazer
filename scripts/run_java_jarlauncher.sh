#!/usr/bin/env bash
# Pass the first argument to wait_for_it and the rest to java

../wait_for_it.sh "$1" && java org.springframework.boot.loader.JarLauncher "${@:2}"