package example.plugin.processor.autorouting

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration

class AutoRoutingValidator(
    private val logger: KSPLogger
) {

    fun isValid(declaration: KSClassDeclaration): Boolean {
        val valid = declaration.declarations
            .any { it is KSClassDeclaration && it.isCompanionObject }

        if (!valid) {
            logger.error("Could not find Companion Object for $declaration " +
                    "a Companion Object needs to declared in the class for auto routing to work", declaration)
        }

        return valid
    }
}