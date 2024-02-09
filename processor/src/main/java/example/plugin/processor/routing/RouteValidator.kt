package example.plugin.processor.routing

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

class RouteValidator(
    private val logger: KSPLogger
) {

    fun isValid(resolver: Resolver, node: KSFunctionDeclaration, types: List<KSClassDeclaration>): Boolean {
        if (!filterExpectedFunction(node)) {
            return false
        }

        // logger.warn("validating: " + node.qualifiedName!!.asString())

        // qualified name of the return type
        val qualifiedName = node.returnType?.resolve()?.declaration?.qualifiedName!!
        // convert return type into class
        val classDeclaration = resolver.getClassDeclarationByName(qualifiedName)

        // loop through super types of each method return type and see if any are acceptable
        classDeclaration?.superTypes?.forEach { type ->
            val resolvedType = type.resolve()
            types.forEach { accepted ->
                val assignable = resolvedType.isAssignableFrom(accepted.asType(listOf()))
                if (assignable) {
                    return true
                }
            }
            // logger.warn("Processed and invalid: ${resolvedType.toClassName()}")
        }

        // if we've not already returned true, it's not valid
        return false
    }

    internal fun filterExpectedFunction(route: KSFunctionDeclaration): Boolean {
        return isCompanionObject(route) || hasContainingFile(route)
    }

    // is declared in a companion object
    private fun isCompanionObject(route: KSFunctionDeclaration) = route.closestClassDeclaration()?.isCompanionObject == true

    // is contained in a class with a package name
    private fun hasContainingFile(route: KSFunctionDeclaration) = route.containingFile != null
}