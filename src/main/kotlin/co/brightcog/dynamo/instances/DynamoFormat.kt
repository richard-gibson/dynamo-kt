package co.brightcog.dynamo.instances

import arrow.TC
import arrow.core.*
import arrow.data.Try
import arrow.syntax.option.*
import arrow.typeclass
import co.brightcog.dynamo.error.DynamoReadError
import co.brightcog.dynamo.error.NoPropertyOfType
import co.brightcog.dynamo.error.TypeCoercionError
import co.brightcog.dynamo.instances.DynamoFormat.Companion.attribute
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import java.math.BigDecimal

@typeclass
interface DynamoFormat<T> : TC {
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


val numberDDBFormat: DynamoFormat<String> = attribute({ it.n }, "N") { a -> { a.withN(it) } }

object StringDDBFormat {
    fun instance(): DynamoFormat<String> =
            attribute({ it.s }, "S") { a -> { a.withS(it) } }

}
object BooleanDDBFormat {
    fun instance(): DynamoFormat<Boolean> =
            attribute({ it.bool }, "BOOL") { a -> { a.withBOOL(it) } }


}

object IntDDBFormat {
    fun instance(): DynamoFormat<Int> =
            DynamoFormat.xmap(numberDDBFormat, DynamoFormat.coerceNumber({ it.toInt() }), { it.toString() })
}

object LongDDBFormat {
    fun instance(): DynamoFormat<Long> =
            DynamoFormat.xmap(numberDDBFormat, DynamoFormat.coerceNumber({ it.toLong() }), { it.toString() })
}

object DoubleDDBFormat {
    fun instance(): DynamoFormat<Double> =
            DynamoFormat.xmap(numberDDBFormat, DynamoFormat.coerceNumber({ it.toDouble() }), { it.toString() })
}

object BigDecimalDDBFormat {
    fun instance(): DynamoFormat<BigDecimal> =
            DynamoFormat.xmap(numberDDBFormat, DynamoFormat.coerceNumber({ BigDecimal(it) }), { it.toString() })
}

object ShortDecimalDDBFormat {
    fun instance(): DynamoFormat<Short> =
            DynamoFormat.xmap(numberDDBFormat, DynamoFormat.coerceNumber({ it.toShort() }), { it.toString() })
}
