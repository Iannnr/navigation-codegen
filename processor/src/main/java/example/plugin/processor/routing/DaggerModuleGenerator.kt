package example.plugin.processor.routing

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile

class DaggerModuleGenerator(
    private val logger: KSPLogger
) {

    fun generateFunSpec(function: KSFunctionDeclaration, routeName: ClassName): FunSpec {
        return FunSpec.builder("binds${routeName.simpleName}")
            .addOriginatingKSFile(function.containingFile!!)
            .addAnnotation(ClassName("dagger", "Binds"))
            .addAnnotation(ClassName("javax.inject", "Singleton"))
            .addParameter(
                ParameterSpec.builder("route", ClassName(routeName.packageName, routeName.simpleName + "Impl"))
                    .build()
            )
            .addModifiers(KModifier.ABSTRACT)
            .returns(routeName)
            .build()
    }
}