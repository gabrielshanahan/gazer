package io.github.gabrielshanahan.gazer.gazer.model

abstract class AbstractModel<T> {
    abstract fun asEntity(): T
}
