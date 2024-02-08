package example.plugin.processor

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import example.plugin.processor.ClassNames.isContext
import example.plugin.processor.ClassNames.isContract
import example.plugin.processor.ClassNames.isIntent

class RouteGenerator(
    private val codeGenerator: CodeGenerator
) {

    fun generate(route: KSFunctionDeclaration, routeName: String) {
        // expects to be declared in a companion object, so finds the parent of that parent companion object
        // should try to find declarations, filter KSClassDeclaration and filter not isCompanionObject...
        val parentClass = route.parentDeclaration?.parentDeclaration?.closestClassDeclaration()?.toClassName()?.simpleName ?: return
        // allow routeName override from params, fall back to class name
        val interfaceName = routeName.ifBlank { parentClass }.replace("activity", "", true).replace("fragment", "", true)

        // makes sure it's annotation on a member function, not a root-level static function, and returns a class
        val isParentCompanionObject = route.closestClassDeclaration()?.isCompanionObject
        val returnType = route.returnType
        if (route.functionKind != FunctionKind.MEMBER || isParentCompanionObject != true || returnType == null) return

        // package + class name for route interface
        val interfaceClass = ClassName(
            route.containingFile!!.packageName.asString(),
            "${interfaceName}NavigationRoute"
        )

        val resolvedType = returnType.resolve()
        val includeContext = resolvedType.isIntent

        // TODO this could be improved
        val methodName = when {
            resolvedType.isIntent -> "getIntent"
            resolvedType.isContract -> "getContract"
            parentClass.contains("fragment", true) -> "getFragment"
            else -> return
        }

        val output = FileSpec.builder(interfaceClass)
            .addType(
                TypeSpec.funInterfaceBuilder(interfaceClass)
                    .addFunction(
                        FunSpec.builder(methodName)
                            .addModifiers(KModifier.ABSTRACT)
                            .apply {
                                // if we need to create an intent route, pass in Context
                                if (includeContext) addParameter("context", ClassNames.context)
                            }
                            .apply {
                                // don't double include context param if it's declared
                                // but include any extra params declared in the annotated function
                                route.parameters
                                    .filter { !it.type.resolve().isContext }
                                    .map { it.name?.getShortName()!! to it.type.toTypeName() }
                                    .forEach { (name, type) -> addParameter(name, type) }
                            }
                            .returns(returnType.toTypeName())
                            .build()
                    )
                    .build()
            )
            .build()

        // TODO if file exists, rewrite by removed SAM and adding other abstract method(s)
        output.writeTo(codeGenerator, false)
    }
}