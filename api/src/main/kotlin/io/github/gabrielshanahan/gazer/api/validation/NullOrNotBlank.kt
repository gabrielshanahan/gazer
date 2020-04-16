package io.github.gabrielshanahan.gazer.api.validation

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [NullOrNotBlankValidator::class])
annotation class NullOrNotBlank(
    val message: String = "{javax.validation.constraints.NotBlank.message}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class NullOrNotBlankValidator : ConstraintValidator<NullOrNotBlank, String?> {

    override fun isValid(str: String?, constraintValidatorContext: ConstraintValidatorContext): Boolean =
        str?.trim()?.isNotEmpty() ?: true
}
