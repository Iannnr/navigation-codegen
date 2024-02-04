package example.plugin.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid

class RouteVisitor(
    codeGenerator: CodeGenerator
): KSVisitorVoid() {

    private val generator = RouteGenerator(codeGenerator)

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        val arguments = function.annotations.iterator().next().arguments
        val routeName = arguments[0].value as String

        generator.generate(
            route = function,
            routeName = routeName
        )
    }
}