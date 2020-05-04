package io.github.gabrielshanahan.gazer.api.security

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Authorized(val enabled: Boolean = true)
