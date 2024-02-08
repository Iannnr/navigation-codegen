package example.plugin.processor

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
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

        val output = FileSpec.builder(interfaceClass)
            .addType(
                TypeSpec.funInterfaceBuilder(interfaceClass)
                    .addFunction(
                        FunSpec.builder(returnType.getMethodName(resolver))
                            .addModifiers(KModifier.ABSTRACT)
                            .apply {
                                val specs = route.parameters
                                    .map {
                                    ParameterSpec.builder(
                                        name = it.name!!.asString(),
                                        type = it.type.toTypeName()
                                    ).build()
                                }
                                addParameters(specs)
                            }
                            .returns(returnType.getReturnType(resolver))
                            .build()
                    )
                    .build()
            )
            .build()

        // TODO if file exists, rewrite by removed SAM and adding other abstract method(s)
        output.writeTo(codeGenerator, false)
    }
}