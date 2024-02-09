package example.plugin.processor.routing

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.validate
import example.plugin.annotation.NavigationRoute

class RouteProcessor(
    environment: SymbolProcessorEnvironment
) : SymbolProcessor {

    private val logger = environment.logger
    private val generator = environment.codeGenerator
    private val validator = RouteValidator(logger)

    lateinit var visitor: RouteVisitor

    private fun getAcceptedRouteTypes(resolver: Resolver): List<KSClassDeclaration> {
        return listOfNotNull(
            resolver.getClassDeclarationByName(ClassNames.ClassPaths.INTENT),
            resolver.getClassDeclarationByName(ClassNames.ClassPaths.FRAGMENT),
            resolver.getClassDeclarationByName(ClassNames.ClassPaths.CONTRACT),
        )
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val annotation = NavigationRoute::class.qualifiedName ?: return emptyList()

        visitor = RouteVisitor(generator, resolver, logger)
        val acceptedTypes = getAcceptedRouteTypes(resolver)

        val files = resolver.getNewFiles()
        logger.warn("new files: ${files.map { it.fileName }.toList()}")

        // get all methods which are annotated as routes
        val resolved = resolver
            .getSymbolsWithAnnotation(annotation)
            .toList()

        val filtered = resolved
            .filter(KSNode::validate)
            .filterIsInstance<KSFunctionDeclaration>()
            .filter { function -> validator.isValid(resolver, function, acceptedTypes) }
            .onEach {
                // tell the route visitor to go and generate the code for this function
                it.accept(visitor, Unit)
            }

        // return a list of unprocessed functions
        return resolved - filtered.toSet()
    }

    override fun finish() {
        super.finish()

        visitor.generateAllSpecs()
    }
}