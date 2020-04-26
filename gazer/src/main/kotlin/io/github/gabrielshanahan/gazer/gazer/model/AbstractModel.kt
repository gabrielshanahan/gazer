package io.github.gabrielshanahan.gazer.gazer.model

/**
 * Base class for all models. A model is understood to be a domain(module)-specific adapter for a (database) entity. The
 * purpose is to loosen the coupling between the persistence and business domains.
 *
 * @see io.github.gabrielshanahan.gazer.data.entity.AbstractEntity
 */
abstract class AbstractModel<T> {
    abstract fun asEntity(): T
}
