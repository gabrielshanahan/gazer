package io.github.gabrielshanahan.gazer.api.security

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Authenticated(val required: Boolean = true)
