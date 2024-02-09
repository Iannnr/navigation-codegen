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
import example.plugin.processor.routing.ClassNames
import example.plugin.processor.routing.ClassNames.ClassPaths.ACTIVITY
import example.plugin.processor.routing.ClassNames.ClassPaths.FRAGMENT

class AutoRouteVisitor(
    private val resolver: Resolver,
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
) : KSVisitorVoid() {

    private val activity = resolver.getClassDeclarationByName(ACTIVITY)!!.asType(listOf())
    private val fragment = resolver.getClassDeclarationByName(FRAGMENT)!!.asType(listOf())

    companion object {
        private val specs = mutableListOf<FunSpec>()
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        val isFragment = classDeclaration.isFragment
        val isActivity = classDeclaration.isActivity

        val methodName: String
        val returnType: ClassName

        if (isActivity) {
            methodName = "getIntent"
            returnType = ClassNames.intent
        } else if (isFragment) {
            methodName = "newInstance"
            returnType = ClassNames.fragment
        } else {
            return
        }

        specs += FunSpec.builder(methodName)
            .addAnnotation(NavigationRoute::class)
            .receiver(classDeclaration.toClassName().nestedClass("Companion"))
            .apply {
                if (isActivity) {
                    addParameter(
                        ParameterSpec.builder("context", ClassNames.context)
                            .build()
                    )
                }
            }
            .returns(returnType)
            .addCode(getImplementationMethod(classDeclaration))
            .build()
    }

    private fun getImplementationMethod(route: KSClassDeclaration): CodeBlock {
        return if (route.isActivity) {
            CodeBlock.of("""return Intent(context, ·%N::class.java)""", route.toClassName().simpleName)
        } else if (route.isFragment) {
            CodeBlock.of("""return ${route.toClassName().simpleName}()""")
        } else {
            CodeBlock.of("")
        }
    }

    fun writeSpecs() {
        val extension = FileSpec.builder("example.plugin.routing", "AutoRoutes")
            .apply {
                specs.forEach {
                    addFunction(it)
                }
            }
            .build()

        try {
            extension.writeTo(codeGenerator, true)
        } catch (e: FileAlreadyExistsException) {
            logger.warn(e.localizedMessage)
        }
    }

    private val KSClassDeclaration.isActivity: Boolean
        get() = getAllSuperTypes().contains(activity)

    private val KSClassDeclaration.isFragment: Boolean
        get() = getAllSuperTypes().contains(fragment)
}