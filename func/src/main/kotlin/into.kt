package io.github.gabrielshanahan.gazer.func

/** Implements a piping operator. */
inline infix fun <T, R> T.into(f: (T) -> R): R = let(f)

/** Suspending version of [into]. */
suspend inline infix fun <T, R> T.suspInto(crossinline f: suspend (T) -> R): R = f(this)
