package example.plugin.processor

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import example.plugin.processor.ClassNames.getMethodName
import example.plugin.processor.ClassNames.getReturnType

class RouteGenerator(
    private val codeGenerator: CodeGenerator,
    private val resolver: Resolver
) {

    fun generate(route: KSFunctionDeclaration, routeName: String) {
        // expects to be declared in a companion object, so finds the parent of that parent companion object
        val parentClass = route.parentDeclaration?.parentDeclaration?.closestClassDeclaration()?.toClassName()?.simpleName ?: return
        val returnType = route.returnType?.resolve() ?: return
        val containingFile = route.containingFile ?: return

        // allow routeName override from params, fall back to class (not companion object) name
        val interfaceName = routeName.ifBlank { parentClass }
            .replace(Regex("([aA]ctivity|[fF]ragment)"), "") // replace android-specific terminology, as routes are agnostic

        // package + class name for route interface
        val interfaceClass = ClassName(
            containingFile.packageName.asString(),
            "${interfaceName}NavigationRoute"
        )

        val functionParameters = route.parameters
            .map {
                ParameterSpec.builder(
                    name = it.name!!.asString(),
                    type = it.type.toTypeName()
                ).build()
            }

        val implementationFunSpec = FunSpec.builder(returnType.getMethodName(resolver))
            .addModifiers(KModifier.OVERRIDE)
            .addParameters(functionParameters)
            .returns(returnType.getReturnType(resolver))
            .addStatement(
                """return ${getImplementationMethod(route)}"""
            )
            .build()

        // TODO if file exists, rewrite by removed SAM and adding other abstract method(s)
        generateInterface(
            classSpec = interfaceClass,
            returnType = returnType,
            params = functionParameters
        )

        // generate the implementation class, override the interface
        generateImplementation(
            parent = interfaceClass,
            overrideFun = implementationFunSpec
        )
    }

    private fun generateInterface(
        classSpec: ClassName,
        returnType: KSType,
        params: List<ParameterSpec>
    ) {
        val interfaceSpec = FileSpec.builder(classSpec)
            .addType(
                TypeSpec.funInterfaceBuilder(classSpec)
                    .addFunction(
                        FunSpec.builder(returnType.getMethodName(resolver))
                            .addModifiers(KModifier.ABSTRACT)
                            .addParameters(params)
                            .returns(returnType.getReturnType(resolver))
                            .build()
                    )
                    .build()
            )
            .build()

        interfaceSpec.writeTo(codeGenerator, false)
    }

    private fun generateImplementation(
        parent: ClassName,
        overrideFun: FunSpec
    ) {
        val implementationSpec = FileSpec.builder(parent.packageName, parent.simpleName + "Impl")
            .addType(
                TypeSpec.classBuilder(parent.simpleName + "Impl")
                    .addModifiers(KModifier.INTERNAL)
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
                    .addFunction(overrideFun)
                    .build()
            )

            .build()

        implementationSpec.writeTo(codeGenerator, false)
    }

    private fun getImplementationMethod(route: KSFunctionDeclaration): String {
        val parent = route.parentDeclaration?.parentDeclaration?.closestClassDeclaration()?.toClassName()?.simpleName
        val params = route.parameters.joinToString { it.name!!.asString() }

        return """
            $parent.${route.simpleName.asString()}($params)
        """.trimIndent()
    }
}