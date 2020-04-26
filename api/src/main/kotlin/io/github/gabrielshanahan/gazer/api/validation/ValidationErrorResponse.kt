package io.github.gabrielshanahan.gazer.api.validation

/**
 * Used to return a validation error response
 *
 * @see ValidationErrorHandlingAdvice
 */
data class ValidationErrorResponse(val violations: List<Violation>) {
    data class Violation(val fieldName: String, val message: String)
}
