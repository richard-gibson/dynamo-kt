package app
import co.brightcog.dynamo.instances.dynamoFormat
import com.amazonaws.services.dynamodbv2.model.AttributeValue


fun main(a:Array<String>){
    val stringFmt  = dynamoFormat<String>()
    val intFmt  = dynamoFormat<Int>()

    println(intFmt.write(123))
    println(stringFmt.write("foo"))
    println(stringFmt.read(AttributeValue().withS("foo")))
    println(intFmt.read(AttributeValue().withN("99")))
    println(intFmt.read(AttributeValue().withN("bar")))

}
