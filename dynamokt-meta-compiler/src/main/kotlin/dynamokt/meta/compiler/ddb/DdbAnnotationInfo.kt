package dynamokt.meta.compiler.ddb

import dynamokt.meta.ddb

val ddbAnnotationKClass = ddb::class
val ddbAnnotationClass = ddbAnnotationKClass.java
val ddbAnnotationName = "@" + ddbAnnotationClass.simpleName
