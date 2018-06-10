package app

import arrow.core.Option
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import dynamokt.*
import dynamokt.instances.*

fun main(a:Array<String>){

    val stringFmt  = String.dynamoFormat()
    val intFmt  = Int.dynamoFormat()
    val empFmt  = Employee.dynamoFormat()

    val e = Employee("first", "last")
    val eDDB = empFmt.write(e)
    val e2 = empFmt.read(eDDB)
    println(intFmt.write(123))
    println(stringFmt.write("foo"))
    println(stringFmt.read(AttributeValue().withS("foo")))
    println(intFmt.read(AttributeValue().withN("99")))
    println(intFmt.read(AttributeValue().withN("bar")))
    println(e)
    println(eDDB)
    println(e2)

    println()
    val m1 = AttributeValue().withM(
        mapOf("foo" to AttributeValue().withS("123"),
              "bar" to AttributeValue().withS("false")))


    val m2 = AttributeValue().withM(
        mapOf("name" to AttributeValue().withS("fName"),
              "lastName" to AttributeValue().withS("lName")))

    println(empFmt.read(m2))
    println(empFmt.read(m1))

/*
    println(ListDc>().write(ListDc(1, listOf("foo", "bar"))))
    println(dynamoFormat<MapDc>().write(MapDc(1, mapOf("foo" to 1, "bar" to 2))))*/
}
