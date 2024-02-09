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


    override fun process(resolver: Resolver): List<KSAnnotated> {
        // environment.logger.warn("process AutoRoute")
        val annotation = AutoRoute::class.qualifiedName ?: return emptyList()

        val visitor = AutoRouteVisitor(environment.logger, environment.codeGenerator)

        val classes = resolver.getSymbolsWithAnnotation(annotation)
            .filterIsInstance<KSClassDeclaration>()
            .toList()
            .onEach {
                it.accept(visitor, Unit)
            }

        return emptyList() // don't consume these
    }
}