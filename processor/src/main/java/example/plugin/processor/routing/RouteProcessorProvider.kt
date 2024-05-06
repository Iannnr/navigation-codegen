package example.plugin.processor.routing

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class RouteProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        printArguments(environment)

        return RouteProcessor(
            environment = environment
        )
    }

    private fun printArguments(environment: SymbolProcessorEnvironment) {
        val args = environment.options

        args.forEach { key, value ->
            environment.logger.warn("$key $value")
        }
    }
}