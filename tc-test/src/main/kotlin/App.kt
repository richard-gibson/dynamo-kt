import test.tc.typeclasses.Read

fun main(a:Array<String>) {

    println(Read{"asdsad"}.read("foo"))
    println(Read<Int>{1}.read("2"))
}