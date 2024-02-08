package example.plugin.processor

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.validate
import example.plugin.annotation.Route

class RouteProcessor(
    logger: KSPLogger,
    private val generator: CodeGenerator
) : SymbolProcessor {

    private val validator = RouteValidator(logger)

    private fun getAcceptedRouteTypes(resolver: Resolver): List<KSClassDeclaration> {
        return listOfNotNull(
            resolver.getClassDeclarationByName(ClassNames.ClassPaths.INTENT),
            resolver.getClassDeclarationByName(ClassNames.ClassPaths.FRAGMENT),
            resolver.getClassDeclarationByName(ClassNames.ClassPaths.CONTRACT),
        )
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotation = Route::class.qualifiedName ?: return emptyList()

        val visitor = RouteVisitor(generator, resolver)
        val acceptedTypes = getAcceptedRouteTypes(resolver)

        // get all methods which are annotated as routes
        val resolved = resolver
            .getSymbolsWithAnnotation(annotation)
            .filterIsInstance<KSFunctionDeclaration>()
            .toList()

        val filtered = resolved
            .filter(KSNode::validate)
            .filter { function -> validator.isValid(resolver, function, acceptedTypes) }
            .onEach {
                // tell the route visitor to go and generate the code for this function
                it.accept(visitor, Unit)
            }

        // return a list of unprocessed functions
        return resolved - filtered.toSet()
    }
}