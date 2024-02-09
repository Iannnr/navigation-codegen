package example.plugin.processor.autorouting

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import example.plugin.annotation.AutoRoute

class AutoRouteProcessor(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {

    private val validator = AutoRoutingValidator(environment.logger)
    private lateinit var visitor: AutoRouteVisitor

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotation = AutoRoute::class.qualifiedName ?: return emptyList()

        visitor = AutoRouteVisitor(resolver, environment.logger, environment.codeGenerator)

        val resolved = resolver.getSymbolsWithAnnotation(annotation)
            .filterIsInstance<KSClassDeclaration>()
            .toList()

        val filtered = resolved
            .filter(validator::isValid)
            .onEach {
                it.accept(visitor, Unit)
            }

        visitor.writeSpecs()

        return resolved - filtered.toSet()
    }
}