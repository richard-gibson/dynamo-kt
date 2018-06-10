package dynamokt.meta.compiler.ddb

import arrow.common.utils.ClassOrPackageDataWrapper
import javax.lang.model.element.TypeElement

class DdbAnnotated(
        val classElement: TypeElement,
        val classOrPackageProto: ClassOrPackageDataWrapper)