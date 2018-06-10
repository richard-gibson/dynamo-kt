package dynamokt.meta.compiler.ddb

import com.google.auto.service.AutoService
import arrow.common.utils.AbstractProcessor
import arrow.common.utils.knownError
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class DdbProcessor : AbstractProcessor() {

    private val ddbAnnotatedList = mutableListOf<DdbAnnotated>()

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(ddbAnnotationClass.canonicalName)

    /**
     * Processor entry point
     */
    override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
        ddbAnnotatedList += roundEnv
                .getElementsAnnotatedWith(ddbAnnotationClass)
                .map { element ->
                    when (element.kind) {
                        ElementKind.CLASS -> processClass(element as TypeElement)
                        else -> knownError("${ddbAnnotationName} can only be used on immutable data classes")
                    }
                }

        if (roundEnv.processingOver()) {
            val generatedDir = File(this.generatedDir!!, ddbAnnotationClass.simpleName).also { it.mkdirs() }
            JsonFileGenerator(generatedDir, ddbAnnotatedList).generate()
        }
    }

    private fun processClass(element: TypeElement): DdbAnnotated {
        val proto = getClassOrPackageDataWrapper(element)
        return DdbAnnotated(element, proto)
    }

}
