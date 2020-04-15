package io.github.gabrielshanahan.gazer.api.dto

abstract class AbstractDTO<T> {
    abstract fun fromEntity(entity: T)
    abstract infix fun transferTo(entity: T): T
    abstract fun isValidEntity(): Boolean
}
