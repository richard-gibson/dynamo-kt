package test.tc.instances

import test.tc.typeclasses.Read

object StringRead {
    fun instance(): Read<String> =
            Read{it}

}