package example.plugin.processor.routing

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.writeTo
import example.plugin.processor.poet.KFunctionPoet

class NavigationInterfaceGenerator(
    private val resolver: Resolver,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) {
    private val functionPoet = KFunctionPoet(resolver, logger)

    fun generateInterface(
        classSpec: ClassName,
        route: KSFunctionDeclaration,
        params: List<ParameterSpec>,
        containingFile: KSFile
    ) {
        val interfaceSpec = functionPoet.getInterfaceFileSpec(classSpec, route, containingFile, params)
        interfaceSpec.writeTo(codeGenerator, true)
    }
}