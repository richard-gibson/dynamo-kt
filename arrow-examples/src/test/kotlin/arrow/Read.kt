package arrow
data class Foo(val bar:String, val baz: Int){
    companion object { }
}
interface Read<out T> {
    fun String.read(): T
    companion object {
        inline operator fun <T> invoke(crossinline fread: (String) -> T): Read<T> = object : Read<T> {
            override fun String.read(): T =
                    fread(this)
        }
    }
}






object stringReadInstance : Read<String> {
    override fun String.read(): String = this
}

object intReadInstance : Read<Int> {
    override fun String.read(): Int = this.toInt()
}


@instance(Foo::class)
interface FooReadInstance: Read<Foo> {
    override fun String.read(): Foo {
      val l = this.split(",")
        return Foo(readAs(stringReadInstance, l[0]), readAs(intReadInstance, l[1] ))
    }
}


inline fun <reified T> readAs(r:Read<T>, s:String) : T =
r.run { s.read() }

fun main(a:Array<String>) {
    println(readAs(stringReadInstance, "sd"))
    println(readAs(Foo.read(), "asdfad,123"))
}
