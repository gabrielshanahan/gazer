package io.github.gabrielshanahan.gazer.api.validation

data class ValidationErrorResponse(val violations: List<Violation>) {
    data class Violation(val fieldName: String, val message: String)
}
