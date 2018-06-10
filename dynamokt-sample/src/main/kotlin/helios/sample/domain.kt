package dynamokt.sample

import dynamokt.meta.ddb

@ddb data class Company(val name: String, val address: Address, val employees: List<Employee>)
@ddb data class Address(val city: String, val street: Street)
@ddb data class Street(val number: Int, val name: String)
@ddb data class Employee(val name: String, val lastName: String)

@ddb
data class Foo(val i: String, val emps: Map<String, Employee>)
