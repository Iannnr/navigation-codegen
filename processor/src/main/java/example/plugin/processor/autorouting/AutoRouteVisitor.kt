package example.plugin.processor.autorouting

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import example.plugin.annotation.NavigationRoute
import example.plugin.processor.poet.KClassPoet
import example.plugin.processor.routing.ClassNames
import example.plugin.processor.routing.ClassNames.ClassPaths.ACTIVITY
import example.plugin.processor.routing.ClassNames.ClassPaths.FRAGMENT
import kotlin.math.log

class AutoRouteVisitor(
    private val resolver: Resolver,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
) : KSVisitorVoid() {

    private val classPoet = KClassPoet(resolver, logger)

    companion object {
        private val specs = mutableListOf<FunSpec>()
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        specs += getFunSpec(classDeclaration)
    }

    private fun getFunSpec(clazz: KSClassDeclaration): FunSpec {
        return FunSpec.builder(classPoet.getFunctionName(clazz))
            .addAnnotation(NavigationRoute::class)
            .receiver(classPoet.getReceiverType(clazz))
            .apply {
                addParameters(classPoet.getParams(clazz))
            }
            .returns(classPoet.getReturnType(clazz))
            .addCode(classPoet.getCodeBlock(clazz))
            .build()
    }

    fun writeSpecs() {
        val clazz = ClassName("example.plugin.routing", "AutoRoutes")
        val extension = classPoet.getFileSpec(clazz, specs)

        try {
            extension.writeTo(codeGenerator, true)
        } catch (e: FileAlreadyExistsException) {
            logger.warn(e.localizedMessage)
        }
    }
}