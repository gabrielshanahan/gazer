package io.github.gabrielshanahan.gazer.func

inline infix fun <T, R> T.into(f: (T) -> R) = f(this)
