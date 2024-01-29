package example.generator

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import example.generator.config.Route
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.Console
import java.io.File
import java.io.IOException

@OptIn(ExperimentalSerializationApi::class)
class Generator {
    operator fun invoke(inputFile: File, outputFile: File) {
        println(inputFile.path)
        val input = try {
            File(inputFile, "routes.json").inputStream().use {
                json.decodeFromStream<List<Route>>(it)
            }
        } catch (s: SerializationException) {
            return System.err.println("WARN: Possible empty JSON key\n${s.stackTraceToString()}")
        } catch (iae: IllegalArgumentException) {
            return iae.printStackTrace()
        } catch (io: IOException) {
            return io.printStackTrace()
        }

        input.forEach {
            val className = ClassName(
                "navigation.routes",
                "${it.from}To${it.to}${it.type.desc}"
            )

            val output = FileSpec.builder(className)
                .addType(
                    TypeSpec.funInterfaceBuilder(className)
                        .addFunction(
                            FunSpec.builder("get${it.type.desc}")
                                .addModifiers(KModifier.ABSTRACT)
                                .addParameter("context", ClassName("android.content", "Context"))
                                .apply {
                                    it.arguments?.forEach { (param, type) ->
                                        addParameter(param, type.className)
                                    }
                                }
                                .returns(it.type.className)
                                .build()
                        )
                        .build()
                )
                .build()

            output.writeTo(outputFile)
            println(outputFile.path)
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
    ignoreUnknownKeys = true
    decodeEnumsCaseInsensitive = true
    isLenient = true
    allowTrailingComma = true
}
