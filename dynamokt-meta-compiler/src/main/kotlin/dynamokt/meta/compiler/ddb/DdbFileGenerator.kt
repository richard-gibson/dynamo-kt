package dynamokt.meta.compiler.ddb

import arrow.common.Package
import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.extractFullName
import arrow.common.utils.removeBackticks
import org.jetbrains.kotlin.serialization.ProtoBuf
import java.io.File

data class DdbElement(
        val `package`: Package,
        val target: DdbAnnotated
) {
    val properties: List<ProtoBuf.Property> = (target.classOrPackageProto as ClassOrPackageDataWrapper.Class).propertyList
    val tparams: List<ProtoBuf.TypeParameter> = target.classOrPackageProto.typeParameters
    val name: String = target.classElement.simpleName.toString()
    val pairs: List<Pair<String, String>> = properties.map {
        val retType = it.returnType.extractFullName(target.classOrPackageProto as ClassOrPackageDataWrapper.Class)
        val pname = target.classOrPackageProto.nameResolver.getString(it.name)
        pname to retType.removeBackticks()
    }
}

class JsonFileGenerator(
        private val generatedDir: File,
        ddbAnnotatedList: List<DdbAnnotated>
) {

    private val ddbElements: List<DdbElement> = ddbAnnotatedList.map {
      DdbElement(it.classOrPackageProto.`package`, it)
    }

    /**
     * Main entry point for json extension generation
     */
    fun generate() {
        ddbElements.forEachIndexed { _, je ->
            val elementsToGenerate = listOf(genEncoderInstance(je))
            val source: String = elementsToGenerate.joinToString(prefix = listOf(
                    "package ${je.`package`}",
                    "",
                    "import arrow.core.*",
                    "import arrow.data.*",
                    "import dynamokt.error.*",
                    "import dynamokt.syntax.*",
                    "import dynamokt.typeclasses.*",
                    "import dynamokt.instances.*",
                    "import com.amazonaws.services.dynamodbv2.model.AttributeValue",
                    ""
            ).joinToString("\n"), separator = "\n", postfix = "\n")
            val file = File(generatedDir, ddbAnnotationClass.simpleName + ".${je.target.classElement.qualifiedName}.kt")
            file.writeText(source)
        }
    }

    private fun tupleToType(je: DdbElement): String =
        je.pairs.map { (p, _) -> p }.joinToString(",", "(",")") +
            " -> " +
        je.pairs.map { (p) -> "$p = $p" }.joinToString(",", "${je.name}(",")")

    private fun genOptFieldsFromMap(je: DdbElement): String =
        je.pairs.map { (p, t) ->
            """|
               |       av.mapFieldAs<$t>($t.dynamoFormat(), "$p").toValidatedNel()
            """.trimMargin()

        }.joinToString(",",
                       "    Validated.applicative<NonEmptyList<PropertyReadError>>(NonEmptyList.semigroup()).map(",
                       """| ) {
                       |   ${tupleToType(je)}
                      | }.fix().toEither().mapLeft { InvalidPropertiesError(it) }
        """.trimMargin()
                      )
    private fun genAttrFromType(je: DdbElement): String =
        je.pairs.map { (p, t) ->
            """|
               |       "$p" to $t.dynamoFormat().write(t.$p)
            """.trimMargin()

        }.joinToString(",",
                       "    AttributeValue().withM(mapOf(",
                       "))"
                      )

    private fun genEncoderInstance(je: DdbElement): String =
            """|
               |interface ${je.name}DynamoFormatInstance: DynamoFormat<${je.name}> {
               |  override fun read(av: AttributeValue): Either<DynamoReadError, ${je.name}> =
               |  ${genOptFieldsFromMap(je)}
               |  override fun write(t: ${je.name}): AttributeValue =
               |  ${genAttrFromType(je)}
               |}
               |
               |object ${je.name}DynamoFormatInstanceImplicits {
               |  fun instance(): ${je.name}DynamoFormatInstance =
               |    object : ${je.name}DynamoFormatInstance {}
               |}
               |
               |fun ${je.name}.Companion.dynamoFormat() =  ${je.name}DynamoFormatInstanceImplicits.instance()
               |
               |""".trimMargin()

}
