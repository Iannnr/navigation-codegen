package example.plugin.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ksp.toClassName

class RouteValidator(
    private val logger: KSPLogger
) {

    fun isValid(resolver: Resolver, node: KSFunctionDeclaration, types: List<KSClassDeclaration>): Boolean {
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
                    logger.info("Processed and accepted: ${resolvedType.toClassName()}")
                    return true
                }
            }
        }

        // if we've not already returned true, it's not valid
        return false
    }
}