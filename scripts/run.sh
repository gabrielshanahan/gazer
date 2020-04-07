#!/usr/bin/env bash

../wait_for_it.sh "$1" && java org.springframework.boot.loader.JarLauncher "${@:2}"