package example.plugin.processor.poet

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toClassName
import example.plugin.processor.routing.ClassNames
import example.plugin.processor.routing.ClassNames.ClassPaths.ACTIVITY
import example.plugin.processor.routing.ClassNames.fragment
import example.plugin.processor.routing.ClassNames.intent

class KClassPoet(
    private val resolver: Resolver,
    private val logger: KSPLogger
) {

    private val activityClassType = resolver.getClassDeclarationByName(ACTIVITY)!!.asType(listOf())

    /**
     * Fragment is a little bit more difficult to resolve, so defaulting to isActivity true/false
     */
    private fun isActivity(clazz: KSClassDeclaration): Boolean {
        return clazz.getAllSuperTypes()
            .contains(activityClassType)
    }

    fun getFileSpec(className: ClassName, funSpec: List<FunSpec>): FileSpec {
        return FileSpec.builder(className)
            .apply {
                funSpec.forEach(::addFunction)
            }
            .build()
    }

    fun getReturnType(clazz: KSClassDeclaration): TypeName {
        return if (isActivity(clazz)) intent else fragment
    }

    fun getFunctionName(clazz: KSClassDeclaration): String {
        return if (isActivity(clazz)) "getIntent" else "newInstance"
    }

    fun getReceiverType(clazz: KSClassDeclaration): ClassName {
        return clazz.toClassName().nestedClass("Companion")
    }

    fun getParams(clazz: KSClassDeclaration): List<ParameterSpec> {
        return if (isActivity(clazz)) {
            listOf(
                ParameterSpec.builder("context", ClassNames.context)
                    .build()
            )
        } else {
            emptyList()
        }
    }

    fun getCodeBlock(clazz: KSClassDeclaration): CodeBlock {
        val className = clazz.toClassName().simpleName
        return if (isActivity(clazz)) {
            CodeBlock.of("""return Intent(context, ·%N::class.java)""", className)
        } else {
            CodeBlock.of("""return ${className}()""")
        }
    }
}