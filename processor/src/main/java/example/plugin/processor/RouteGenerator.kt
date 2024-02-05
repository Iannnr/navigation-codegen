package example.plugin.processor

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

class RouteGenerator(
    private val codeGenerator: CodeGenerator
) {

    private val context = ClassName("android.content", "Context")
    private val intent = ClassName("android.content", "Intent")
    private val fragment = ClassName("androidx.fragment.app", "Fragment")
    private val deprecatedFragment = ClassName("android.app", "Fragment")
    private val contract = ClassName("androidx.activity.result.contract", "ActivityResultContract").parameterizedBy(STAR, STAR)

    private val KSType.isContext: Boolean
        get() = toClassName() == context

    private val KSType.isIntent: Boolean
        get() = toClassName() == intent

    private val KSType.isContract: Boolean
        get() = toClassName().parameterizedBy(STAR, STAR) == contract

    private val KSType.isFragment: Boolean
        get() = toClassName() == fragment || toClassName() == deprecatedFragment // needs to be instanceOf fragment

    fun generate(route: KSFunctionDeclaration, routeName: String) {
        // expects to be declared in a companion object, so finds the parent of that parent companion object
        // should try to find declarations, filter KSClassDeclaration and filter not isCompanionObject...
        val parentClass = route.parentDeclaration?.parentDeclaration?.closestClassDeclaration()?.toClassName()?.simpleName ?: return
        // allow routeName override from params, fall back to class name
        val interfaceName = routeName.ifBlank { parentClass }

        // makes sure it's annotation on a member function, not a root-level static function
        val isParentCompanionObject = route.closestClassDeclaration()?.isCompanionObject
        if (route.functionKind != FunctionKind.MEMBER || isParentCompanionObject != true) return

        // package + class name for route interface
        val interfaceClass = ClassName(
            route.containingFile!!.packageName.asString(),
            "${interfaceName}NavigationRoute"
        )

        // require a return type (should be either Intent, Fragment or Contract)
        val returnType = route.returnType ?: return

        val resolvedType = returnType.resolve()
        val includeContext = resolvedType.isIntent

        val methodName = when {
            resolvedType.isIntent -> "getIntent"
            resolvedType.isFragment -> "getFragment"
            resolvedType.isContract -> "getContract"
            else -> throw UnsupportedOperationException("${resolvedType.declaration.simpleName.getShortName()} is not a supported type")
        }

        val output = FileSpec.builder(interfaceClass)
            .addType(
                TypeSpec.funInterfaceBuilder(interfaceClass)
                    .addFunction(
                        FunSpec.builder(methodName)
                            .addModifiers(KModifier.ABSTRACT)
                            .apply {
                                // if we need to create an intent route, pass in Context
                                if (includeContext) addParameter("context", context)
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

        output.writeTo(codeGenerator, false)
    }
}