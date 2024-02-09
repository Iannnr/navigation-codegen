package example.plugin.processor.autorouting

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import example.plugin.annotation.NavigationRoute
import example.plugin.processor.routing.ClassNames

class AutoRouteVisitor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
) : KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

        val extension = FileSpec.builder(classDeclaration.packageName.getQualifier() + ".routing", "AutoRoutes")
            .addFunction(
                FunSpec.builder("getIntent")
                    .addAnnotation(NavigationRoute::class)
                    .receiver(classDeclaration.toClassName())
                    .addParameter(
                        ParameterSpec.builder("context", ClassNames.context)
                            .build()
                    )
                    .returns(ClassNames.intent)
                    .addCode(
                        CodeBlock.of(
                            """return Intent(context, ·%N::class.java)""",
                            classDeclaration.parentDeclaration?.closestClassDeclaration()?.toClassName()?.simpleName
                        )
                    )
                    .build()
            )
            .build()

        extension.writeTo(codeGenerator, true)
    }
}