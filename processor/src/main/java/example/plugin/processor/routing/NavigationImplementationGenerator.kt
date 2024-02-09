package example.plugin.processor.routing

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import example.plugin.processor.routing.ClassNames.getMethodName
import example.plugin.processor.routing.ClassNames.getReturnType

class NavigationImplementationGenerator(
    private val resolver: Resolver,
    private val codeGenerator: CodeGenerator
) {

    fun generateImplementation(
        parent: ClassName,
        containingFile: KSFile,
        returnType: KSType,
        params: List<ParameterSpec>,
        route: KSFunctionDeclaration,
    ) {
        val implementationSpec = FileSpec.builder(parent.packageName, parent.simpleName + "Impl")
            .addType(
                TypeSpec.classBuilder(parent.simpleName + "Impl")
                    .addOriginatingKSFile(containingFile)
                    .addSuperinterface(parent)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addAnnotation(
                                AnnotationSpec.builder(
                                    ClassName("javax.inject", "Inject")
                                )
                                    .build()
                            )
                            .build()
                    )
                    .addFunction(
                        createFunSpec(
                            returnType = returnType,
                            containingFile = containingFile,
                            params = params,
                            route = route
                        )
                    )
                    .build()
            )
            .build()

        implementationSpec.writeTo(codeGenerator, true)
    }

    private fun createFunSpec(
        returnType: KSType,
        containingFile: KSFile,
        params: List<ParameterSpec>,
        route: KSFunctionDeclaration,
    ): FunSpec {
        // generate the implementation class, override the interface
        return FunSpec.builder(returnType.getMethodName(resolver))
            .addOriginatingKSFile(containingFile)
            .addModifiers(KModifier.OVERRIDE)
            .addParameters(params)
            .returns(returnType.getReturnType(resolver))
            .addStatement(
                """return ${getImplementationMethod(route)}"""
            )
            .build()
    }

    private fun getImplementationMethod(route: KSFunctionDeclaration): String {
        val parentClass = if (route.extensionReceiver != null) {
            route.extensionReceiver?.resolve()?.declaration?.parentDeclaration?.closestClassDeclaration()?.toClassName()
        } else {
            route.parentDeclaration?.parentDeclaration?.closestClassDeclaration()?.toClassName()
        }
        val params = route.parameters.joinToString { it.name!!.asString() }

        return """
            ${parentClass?.simpleName}.${route.simpleName.asString()}($params)
        """.trimIndent()
    }
}