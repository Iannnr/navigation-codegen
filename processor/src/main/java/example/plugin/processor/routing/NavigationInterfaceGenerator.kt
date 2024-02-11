package example.plugin.processor.routing

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import example.plugin.processor.poet.KFunctionPoet
import example.plugin.processor.routing.ClassNames.getMethodName
import example.plugin.processor.routing.ClassNames.getReturnType

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