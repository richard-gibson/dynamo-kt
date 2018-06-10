package test.tc.typeclasses

import arrow.core.*
import kotlin.reflect.KClass


data class ReadError(val t:Throwable) {
    companion object {
        inline operator fun <reified T> invoke(strRepr: String, clz: Class<T>) =
            ReadError(Throwable(message = "unable to read string $strRepr to instance of class ${clz.name}"))
        inline operator fun <reified T : Any> invoke(strRepr: String, kClz: KClass<T>) =
                ReadError(Throwable(message = "unable to read string $strRepr to instance of class ${kClz::java.name}"))
    }
}

interface Read<out T> {
    fun read(s: String): Either<ReadError, T>

    companion object {

        inline operator fun <reified T> invoke(crossinline fRead: (String) -> T): Read<T> = object : Read<T> {
            override fun read(s: String): Either<ReadError, T> =
                    Try{ fRead(s) }.toEither().mapLeft { ReadError.invoke(s, T::class.java) }
        }
    }

}