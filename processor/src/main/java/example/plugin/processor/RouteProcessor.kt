package example.plugin.processor

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
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
        val types = listOfNotNull(
            resolver.getClassDeclarationByName(ClassNames.ClassPaths.INTENT),
            resolver.getClassDeclarationByName(ClassNames.ClassPaths.FRAGMENT),
            resolver.getClassDeclarationByName(ClassNames.ClassPaths.CONTRACT),
        )
        val annotation = Route::class.qualifiedName ?: return emptyList()

        val resolved = resolver
            .getSymbolsWithAnnotation(annotation)
            .filterIsInstance<KSFunctionDeclaration>()
            .toList()

        val filtered = resolved
            .filter(KSNode::validate)
            .filter { function -> validator.isValid(resolver, function, types) }
            .onEach {
                // tell the route visitor to go and generate the code for this function
                it.accept(visitor, Unit)
            }

        // return a list of unprocessed functions
        return resolved - filtered.toSet()
    }
}