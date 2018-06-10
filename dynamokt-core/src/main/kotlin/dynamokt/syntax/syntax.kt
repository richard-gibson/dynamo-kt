package dynamokt.syntax

import arrow.core.*
import arrow.data.*
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import dynamokt.error.*
import dynamokt.typeclasses.*

//inline fun <reified T> T.toAttributeValue(): AttributeValue = dynamoFormat<T>().write(this)
//inline fun <reified T> AttributeValue.read(): Either<DynamoReadError, T> = dynamoFormat<T>().read(this)

inline fun <A, B> Either<A, B>.toValidatedNel(): ValidatedNel<A, B> =
    this.fold({ Invalid(Nel.of(it)) }, { Valid(it) })

inline fun <reified T> AttributeValue.mapFieldAs(dynamoFormat: DynamoFormat<T>, field: String): Either<PropertyReadError, T> =
    this.m[field].toOption().fold(
        { PropertyReadError(field, MissingProperty).left() },
        { dynamoFormat.read(it).mapLeft { PropertyReadError("read error", it) } })