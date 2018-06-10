package dynamokt.sample

import dynamokt.typeclasses.dynamoFormat





fun main(args: Array<String>) {
    val foo = Foo("bar", mapOf("1" to Employee("John", "Doe"),
                               "2" to Employee("John", "Foo"),
                               "3" to Employee("John", "Baz")
                              ))


    val company = Company(name = "Arrow",
                           address = Address("Functional city", Street(23, "lambda street")),
                           employees = listOf(
                               Employee("John", "Doe"),
                               Employee("John", "Foo"),
                               Employee("John", "Baz")
                                             ))

    println(dynamoFormat<Foo>().write(foo))
    println(dynamoFormat<Company>().write(company))


}
