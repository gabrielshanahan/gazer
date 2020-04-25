package io.github.gabrielshanahan.gazer.api.exceptions

sealed class GazerException(val gazerMsg: String) :
    RuntimeException(gazerMsg)

class InvalidGazerTokenException :
    GazerException("Invalid GazerToken")

open class EntityNotFoundException(entity: String, id: String) :
    GazerException("$entity $id not found")

open class EntityForbidden(entity: String, id: String) :
    GazerException("You do not have permission to access $entity $id")
