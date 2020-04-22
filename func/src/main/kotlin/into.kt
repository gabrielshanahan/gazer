package io.github.gabrielshanahan.gazer.func

inline infix fun <T, R> T.into(f: (T) -> R): R = let(f)

suspend inline infix fun <T, R> T.suspInto(crossinline f: suspend (T) -> R): R = f(this)
