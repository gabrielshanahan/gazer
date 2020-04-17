package io.github.gabrielshanahan.gazer.api.model

abstract class AbstractModel<T> {
    abstract fun fromEntity(entity: T)
    abstract infix fun transferTo(entity: T): T
}
