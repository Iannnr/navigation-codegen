package example.plugin.processor.poet

import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.FunSpec

object PoetWriter {

    val routes = listOf<FunSpec>()
    val interfaces = listOf<FunSpec>()
    val implementations = listOf<FunSpec>()
    val modules = listOf<FunSpec>()

    fun writeInterface(codeGenerator: CodeGenerator) {

    }

    fun writeImplementation(codeGenerator: CodeGenerator) {

    }

    fun writeDaggerModule(codeGenerator: CodeGenerator) {

    }

    fun writeAutoRoute(codeGenerator: CodeGenerator) {

    }
}