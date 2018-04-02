package java_lang

import co.brightcog.dynamo.instances.DynamoFormat
import co.brightcog.dynamo.instances.IntDDBFormat
import co.brightcog.dynamo.instances.StringDDBFormat

object StringDynamoFormatInstanceImplicits {
    @JvmStatic
    fun instance(): DynamoFormat<String> = StringDDBFormat.instance()

}

object IntegerDynamoFormatInstanceImplicits {
    @JvmStatic
    fun instance(): DynamoFormat<Int> = IntDDBFormat.instance()
}