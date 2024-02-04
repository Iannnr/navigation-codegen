package example.plugin.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.validate
import example.plugin.annotation.Route

class RouteProcessor(
    logger: KSPLogger,
    generator: CodeGenerator
) : SymbolProcessor {

    private val visitor = RouteVisitor(generator)
    private val validator = RouteValidator(logger)

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotationName = Route::class.qualifiedName ?: return emptyList()

        val resolved = resolver
            .getSymbolsWithAnnotation(annotationName)
            .toList()

        val validated = resolved
            .filter(KSNode::validate)
            .filter(validator::isValid)
            .onEach {
                it.accept(visitor, Unit)
            }

        return resolved - validated.toSet()
    }
}