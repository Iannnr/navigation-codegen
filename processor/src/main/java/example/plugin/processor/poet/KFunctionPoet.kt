package example.plugin.processor.poet

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import example.plugin.processor.routing.ClassNames.ClassPaths.FRAGMENT
import example.plugin.processor.routing.ClassNames.contract
import example.plugin.processor.routing.ClassNames.getMethodName
import example.plugin.processor.routing.ClassNames.getReturnType
import example.plugin.processor.routing.ClassNames.intent
import example.plugin.processor.routing.ClassNames.isFragment

class KFunctionPoet(
    private val resolver: Resolver,
    private val logger: KSPLogger
) {

    /**
     * defines the return type based on the given function
     * In particular will use the return type of Fragment instead of MainFragment
     */
    fun getReturnType(function: KSFunctionDeclaration): TypeName {
        val returnType = function.returnType?.resolve()!!
        return when {
            returnType.toTypeName() == intent -> returnType.toTypeName()
            returnType.toClassName().parameterizedBy(STAR, STAR) == contract -> returnType.toTypeName()
            returnType.isFragment(resolver) -> resolver.getClassDeclarationByName(FRAGMENT)!!.toClassName()
            else -> throw UnsupportedOperationException("${returnType.toClassName()} is not a supported type")
        }
    }

    /**
     * creates the following
     * ```
     * fun getIntent
     * ```
     */
    fun getFunctionName(returnType: KSType): String {
        return when {
            returnType.toTypeName() == intent -> "getIntent"
            returnType.toClassName().parameterizedBy(STAR, STAR) == contract -> "getContract"
            returnType.isFragment(resolver) -> "getFragment"
            else -> throw UnsupportedOperationException("${returnType.toClassName()} is not a supported type")
        }
    }

    /**
     * Creates the following
     * ```
     * MainActivity.getIntent(context, id)
     * ```
     */
    fun getFunctionCodeBlock(function: KSFunctionDeclaration): CodeBlock {
        val parentClass = if (function.extensionReceiver != null) {
            function.extensionReceiver?.resolve()?.declaration?.parentDeclaration?.closestClassDeclaration()?.toClassName()
        } else {
            function.parentDeclaration?.parentDeclaration?.closestClassDeclaration()?.toClassName()
        }
        val params = function.parameters.joinToString { it.name!!.asString() }

        return CodeBlock.of("""
            return ${parentClass?.simpleName}.${function.simpleName.asString()}($params)
        """.trimIndent())
    }

    /**
     * Creates the following
     * ```
     * override fun getIntent(context: Context, id: UUID): Intent = MainActivity.getIntent(context, id)
     * ```
     */
    private fun createFunSpec(
        returnType: KSType,
        containingFile: KSFile,
        params: List<ParameterSpec>,
        route: KSFunctionDeclaration,
    ): FunSpec {
        // generate the implementation class, override the interface
        return FunSpec.builder(getFunctionName(returnType))
            .addOriginatingKSFile(containingFile)
            .addModifiers(KModifier.OVERRIDE)
            .addParameters(params)
            .returns(getReturnType(route))
            .addCode(getFunctionCodeBlock(route))
            .build()
    }

    /**
     * Creates the following
     * ```
     * public class ExampleNavigationRouteImpl @Inject constructor() : ExampleNavigationRoute {
     *   override fun getIntent(context: Context, id: UUID): Intent = MainActivity.getIntent(context, id)
     * }
     * ```
     */
    fun getImplementationFileSpec(
        className: ClassName,
        route: KSFunctionDeclaration,
        containingFile: KSFile,
        params: List<ParameterSpec>
    ): FileSpec {
        val fileClassName = ClassName(className.packageName, className.simpleName + "Impl")

        return FileSpec.builder(fileClassName)
            .addType(
                TypeSpec.classBuilder(fileClassName)
                    .addOriginatingKSFile(containingFile)
                    .addSuperinterface(className)
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addAnnotation(
                                AnnotationSpec.builder(
                                    ClassName("javax.inject", "Inject")
                                ).build()
                            )
                            .build()
                    )
                    .addFunction(createFunSpec(route.returnType!!.resolve(), containingFile, params, route))
                    .build()
            )
            .build()
    }

    fun getInterfaceFileSpec(
        className: ClassName,
        route: KSFunctionDeclaration,
        containingFile: KSFile,
        params: List<ParameterSpec>
    ): FileSpec {
        return FileSpec.builder(className)
            .addType(
                TypeSpec.funInterfaceBuilder(className)
                    .addOriginatingKSFile(containingFile)
                    .addFunction(
                        FunSpec.builder(getFunctionName(route.returnType!!.resolve()))
                            .addOriginatingKSFile(containingFile)
                            .addModifiers(KModifier.ABSTRACT)
                            .addParameters(params)
                            .returns(getReturnType(route))
                            .build()
                    )
                    .build()
            )
            .build()
    }
}