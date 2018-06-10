package dynamokt.instances

import arrow.core.*
import arrow.data.*
import arrow.data.applicative
import arrow.instance
import arrow.instances.traverse
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import dynamokt.error.DynamoReadError
import dynamokt.typeclasses.DynamoFormat
import dynamokt.typeclasses.DynamoFormat.Companion.attribute
import dynamokt.typeclasses.DynamoFormat.Companion.xmap
import java.math.BigDecimal

val numberDDBFormat: DynamoFormat<String> = attribute({ it.n }, "N") { a -> { a.withN(it) } }
val listDDBFormat: DynamoFormat<List<AttributeValue>> = attribute({ it.l }, "L") { a -> { a.withL(it) } }
val mapDDBFormat: DynamoFormat<Map<String, AttributeValue>> = attribute({ it.m }, "M") { a -> { a.withM(it) } }



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
      xmap(numberDDBFormat, DynamoFormat.coerceNumber({ it.toInt() }), { it.toString() })
}

object LongDDBFormat {
  fun instance(): DynamoFormat<Long> =
      xmap(numberDDBFormat, DynamoFormat.coerceNumber({ it.toLong() }), { it.toString() })
}

object DoubleDDBFormat {
  fun instance(): DynamoFormat<Double> =
      xmap(numberDDBFormat, DynamoFormat.coerceNumber({ it.toDouble() }), { it.toString() })
}

object BigDecimalDDBFormat {
  fun instance(): DynamoFormat<BigDecimal> =
      xmap(numberDDBFormat, DynamoFormat.coerceNumber({ BigDecimal(it) }), { it.toString() })
}

object ShortDDBFormat {
  fun instance(): DynamoFormat<Short> =
      xmap(numberDDBFormat, DynamoFormat.coerceNumber({ it.toShort() }), { it.toString() })
}
//ListDynamoFormatInstanceImplicits
/*
object ListDDBFormat {

  fun <T> instance(dynamoFormatT: DynamoFormat<T>): DynamoFormat<List<T>> =
      xmap<List<T>, List<AttributeValue>>(
          listDDBFormat,
          { it.k().traverse(Either.applicativeError(),  { a -> dynamoFormatT.read(a) }).fix() },
          { it.map { a -> dynamoFormatT.write(a) } })
}

object MapDDBFormat {
  fun <V> instance(dynamoFormatT: DynamoFormat<V>): DynamoFormat<Map<String, V>> =
      xmap<Map<String, V>, Map<String, AttributeValue>>(
          mapDDBFormat,
          { it.k().traverse(Either.applicative(), { a -> dynamoFormatT.read(a) }).fix() },
          { it.mapValues { (_, v) -> dynamoFormatT.write(v) } })
}
*/


@instance(Option::class)
interface OptionDynamoFormatInstance<A> : DynamoFormat<Option<A>> {
    fun dynamoFormat(): DynamoFormat<A>
    override fun read(av: AttributeValue): Either<DynamoReadError, Option<A>> =
            av.toOption()
                    .filterNot { it.isNULL }
                    .traverse({ dynamoFormat().read(it) }, Either.applicativeError()).fix()

    override fun write(t: Option<A>): AttributeValue =
            t.fold({AttributeValue().withNULL(true)}, { dynamoFormat().write(it) })
}


/*implicit def optionFormat[T](implicit f: DynamoFormat[T]) = new DynamoFormat[Option[T]] {
    def read(av: AttributeValue): Either[DynamoReadError, Option[T]] = {
        Option(av).filter(x => !Boolean.unbox(x.isNULL)).map(f.read(_).map(Some(_)))
        .getOrElse(Right(Option.empty[T]))
    }

    def write(t: Option[T]): AttributeValue = t.map(f.write).getOrElse(null)
    override val default = Some(None)
}*/


fun String.Companion.dynamoFormat(): DynamoFormat<String> = StringDDBFormat.instance()
fun Int.Companion.dynamoFormat(): DynamoFormat<Int> = IntDDBFormat.instance()
fun Long.Companion.dynamoFormat(): DynamoFormat<Long> = LongDDBFormat.instance()
fun Double.Companion.dynamoFormat(): DynamoFormat<Double> = DoubleDDBFormat.instance()
fun Short.Companion.dynamoFormat(): DynamoFormat<Short> = ShortDDBFormat.instance()

