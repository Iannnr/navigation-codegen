package example.plugin.processor.routing

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.writeTo
import example.plugin.processor.routing.ClassNames.getMethodName
import example.plugin.processor.routing.ClassNames.getReturnType

class NavigationInterfaceGenerator(
    private val resolver: Resolver,
    private val codeGenerator: CodeGenerator
) {

    fun generateInterface(
        classSpec: ClassName,
        returnType: KSType,
        params: List<ParameterSpec>,
        containingFile: KSFile
    ) {
        val interfaceSpec = FileSpec.builder(classSpec)
            .addType(
                TypeSpec.funInterfaceBuilder(classSpec)
                    .addOriginatingKSFile(containingFile)
                    .addFunction(
                        FunSpec.builder(returnType.getMethodName(resolver))
                            .addOriginatingKSFile(containingFile)
                            .addModifiers(KModifier.ABSTRACT)
                            .addParameters(params)
                            .returns(returnType.getReturnType(resolver))
                            .build()
                    )
                    .build()
            )
            .build()

        interfaceSpec.writeTo(codeGenerator, true)
    }
}