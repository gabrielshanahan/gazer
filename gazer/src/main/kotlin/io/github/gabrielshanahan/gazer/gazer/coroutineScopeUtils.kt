package io.github.gabrielshanahan.gazer.gazer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking

/** Helper function to cancel an entire coroutine scope. */
internal fun CoroutineScope.cancelAndBlock() = runBlocking {
    this@cancelAndBlock.coroutineContext[Job]?.cancelAndJoin()
}
