package example.plugin.processor.routing

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
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

class RouteGenerator(
    private val codeGenerator: CodeGenerator,
    private val resolver: Resolver,
    private val logger: KSPLogger
) {

    private val noop = FunSpec.builder("")
        .build()

    fun generateFunSpec(route: KSFunctionDeclaration, routeName: String): FunSpec {
        val parentClass = if (route.extensionReceiver != null) {
            route.extensionReceiver?.resolve()?.declaration?.parentDeclaration?.closestClassDeclaration()?.toClassName()
        } else {
            route.parentDeclaration?.parentDeclaration?.closestClassDeclaration()?.toClassName()
        }

        // expects to be declared in a companion object, so finds the parent of that parent companion object
        val parent = parentClass?.simpleName ?: return noop
        val returnType = route.returnType?.resolve() ?: return noop
        val containingFile = route.containingFile ?: return noop

        logger.warn("parent: $parent")

        // allow routeName override from params, fall back to class (not companion object) name
        val interfaceName = routeName.ifBlank { parent }
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

        // TODO if file exists, rewrite by removed SAM and adding other abstract method(s)
        NavigationInterfaceGenerator(resolver, codeGenerator)
            .generateInterface(
                classSpec = interfaceClass,
                returnType = returnType,
                params = functionParameters,
                containingFile = containingFile
            )

        NavigationImplementationGenerator(resolver, codeGenerator)
            .generateImplementation(
                parent = interfaceClass,
                containingFile = containingFile,
                returnType = returnType,
                params = functionParameters,
                route = route
            )

        return DaggerModuleGenerator(logger)
            .generateFunSpec(route, interfaceClass)
    }

    fun generateDagger(specs: List<FunSpec>) {
        val moduleSpec = FileSpec.builder("example.plugin.routing", "NavigationRouteModule")
            .addType(
                TypeSpec.classBuilder("NavigationRouteModule")
                    .addModifiers(KModifier.ABSTRACT)
                    .addAnnotation(ClassName("dagger", "Module"))
                    .addFunctions(specs)
                    .build()
            )
            .build()

        moduleSpec.writeTo(codeGenerator, true)
    }
}