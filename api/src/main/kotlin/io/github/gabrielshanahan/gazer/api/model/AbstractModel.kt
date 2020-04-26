package io.github.gabrielshanahan.gazer.api.model

/**
 * Base class for all models. A model is understood to be a domain(module)-specific adapter for a (database) entity. The
 * purpose is to loosen the coupling between the persistence and business domains.
 *
 * @see io.github.gabrielshanahan.gazer.data.entity.AbstractEntity
 */
abstract class AbstractModel<T> {
    /** Creates model from [entity] **/
    abstract fun fromEntity(entity: T)

    /** Transfers contents of model to [entity] */
    abstract infix fun transferTo(entity: T): T
}
