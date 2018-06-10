package dynamokt

import arrow.core.Option
import dynamokt.meta.ddb
@ddb data class Street(val number: Int, val name: String) {
    companion object {}
}
@ddb data class Address(val city: String, val street: Street){
    companion object {}
}
@ddb data class Company(val name: String, val address: Address){
    companion object {}
}
@ddb data class Employee(val name: String, val company: String){
    companion object {}
}


/*
@ddb data class ListDc(val i: Int, val l: List<String>) {
    companion object {}
}
@ddb data class MapDc(val i: Int, val l: Map<String, Int>) {
    companion object {}
}*/


@ddb
data class ListDc(val i: Int, val j: Option<Int>) {
    companion object {}
}
