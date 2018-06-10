package dynamokt.error

import arrow.data.NonEmptyList
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException

sealed class DynamoError
data class ConditionNotMet(val e: ConditionalCheckFailedException) : DynamoError()

sealed class DynamoReadError : DynamoError() {
  companion object {
    fun describe(dre: DynamoReadError): String = when (dre) {
      is InvalidPropertiesError -> dre.errors.all.map { "'${it.name}': ${describe(it.problem)}" }.joinToString { ", " }
      is NoPropertyOfType -> "not of type: '${dre.propertyType}' was '${dre.actual}'"
      is TypeCoercionError -> "could not be converted to desired type: ${dre.t}"
      is MissingProperty -> "missing"
    }
  }
}
data class NoPropertyOfType(val propertyType: String, val actual: AttributeValue) : DynamoReadError()
data class TypeCoercionError(val t: Throwable) : DynamoReadError()
object MissingProperty: DynamoReadError()

data class PropertyReadError(val name: String, val problem: DynamoReadError)
data class InvalidPropertiesError(val errors: NonEmptyList<PropertyReadError>) : DynamoReadError() {
  companion object {}
}
/*@instance(InvalidPropertiesError::class)
interface InvalidPropertiesErrorSemigroupInstance : Semigroup<InvalidPropertiesError> {
  override fun combine(a: InvalidPropertiesError, b: InvalidPropertiesError): InvalidPropertiesError =
      InvalidPropertiesError(a.errors.combine(b.errors))
}*/

//TODO show with arrow 0.7
/*companion object {
    implicit object ShowInstance: Show<DynamoReadError> {
        fun show(e: DynamoReadError): String = describe(e)
    }*/

