package example.plugin.processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotated

class RouteValidator(
    private val logger: KSPLogger
) {

    fun isValid(symbol: KSAnnotated): Boolean {
        // enforce return types, params, function names, class declaration etc
        return true
    }
}