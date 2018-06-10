package dynamokt.typeclasses

import arrow.core.*

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import dynamokt.error.DynamoReadError
import dynamokt.error.NoPropertyOfType
import dynamokt.error.TypeCoercionError


interface DynamoFormat<T> {
  fun read(av: AttributeValue): Either<DynamoReadError, T>
  fun write(t: T): AttributeValue
  fun default(): Option<T> = None

  companion object {
    fun <T> attribute(decode: (AttributeValue) -> T, propertyType: String,
                      encode: (AttributeValue) -> (T) -> AttributeValue
                     ): DynamoFormat<T> = object : DynamoFormat<T> {
      override fun read(av: AttributeValue): Either<DynamoReadError, T> =
          decode(av).toOption().toEither { NoPropertyOfType(propertyType, av) }

      override fun write(t: T): AttributeValue = encode(AttributeValue())(t)
    }

    fun <A, B> xmap(f: DynamoFormat<B>, r: (B) -> Either<DynamoReadError, A>, w: (A) -> B) = object : DynamoFormat<A> {
      override fun read(av: AttributeValue): Either<DynamoReadError, A> = f.read(av).flatMap(r)
      override fun write(t: A): AttributeValue = f.write(w(t))
    }

    inline fun <A, B, reified T : Throwable> coerce(crossinline f: (A) -> B): (A) -> Either<DynamoReadError, B> = { a ->
      Try { f(a) }.toEither().mapLeft { TypeCoercionError(it) }
    }

    inline fun <N> coerceNumber(crossinline f: (String) -> N): (String) -> Either<DynamoReadError, N> =
        coerce<String, N, NumberFormatException>(f)

  }
}