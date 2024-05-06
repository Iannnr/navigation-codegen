package example.plugin.processor.routing

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.FunSpec

class RouteVisitor(
    codeGenerator: CodeGenerator,
    resolver: Resolver,
    private val logger: KSPLogger,
) : KSVisitorVoid() {

    companion object {
        private val funSpecs = mutableListOf<FunSpec>()
    }

    private val generator = RouteGenerator(codeGenerator, resolver, logger)

    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
        // logger.warn("visitFunctionDeclaration")

        val arguments = function.annotations.iterator().next().arguments
        val routeName = arguments[0].value as String

        funSpecs += generator.generateFunSpec(
            route = function,
            routeName = routeName
        )
    }

    fun generateAllSpecs() {
        generator.generateDagger(funSpecs)
    }
}