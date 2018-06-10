package java_lang

import test.tc.typeclasses.Read
import test.tc.instances.*


object StringReadInstanceImplicits {
    @JvmStatic
    fun instance(): Read<String> = StringRead.instance()

}
