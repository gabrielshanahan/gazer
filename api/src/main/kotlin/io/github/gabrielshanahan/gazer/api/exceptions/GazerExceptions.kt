package io.github.gabrielshanahan.gazer.api.exceptions

/**
 * Generic domain specific exception.
 */
sealed class GazerException(val gazerMsg: String) :
    RuntimeException(gazerMsg)

/**
 * Thrown when no user with provided token is found.
 */
class InvalidGazerTokenException :
    GazerException("Invalid GazerToken")

/**
 * Thrown when no entity with given id is found.
 *
 * @param entity The entity not found. Should be set by subclasses.
 * @param id The id. Should be passed when thrown.
 */
open class EntityNotFoundException(entity: String, id: String) :
    GazerException("$entity $id not found")

/**
 * Thrown when attempting to access endpoint that doesn't belong to current user.
 *
 * @param entity The entity that was accessed. Should be set by subclasses.
 * @param id The id. Should be passed when thrown.
 */
open class EntityForbidden(entity: String, id: String) :
    GazerException("You do not have permission to access $entity $id")
