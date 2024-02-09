package example.plugin.processor

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import example.plugin.processor.ClassNames.ClassPaths.FRAGMENT

object ClassNames {

    object ClassPaths {
        internal const val FRAGMENT = "androidx.fragment.app.Fragment"
        internal const val INTENT = "android.content.Intent"
        internal const val CONTRACT = "androidx.activity.result.contract.ActivityResultContract"
    }

    internal val intent = ClassName("android.content", "Intent")
    internal val contract = ClassName("androidx.activity.result.contract", "ActivityResultContract").parameterizedBy(STAR, STAR)

    /// region checks to see if the return type is any of the expected types we use for navigation
    internal val KSType.isIntent: Boolean
        get() = toClassName() == intent

    internal val KSType.isContract: Boolean
        get() = toClassName().parameterizedBy(STAR, STAR) == contract

    internal fun KSType.isFragment(resolver: Resolver): Boolean {
        val fragmentClass = resolver.getClassDeclarationByName(FRAGMENT)!!.asType(listOf())
        val returnDeclaration = resolver.getClassDeclarationByName(declaration.qualifiedName!!)!!

        return returnDeclaration.superTypes.any { it.resolve().isAssignableFrom(fragmentClass) }
    }
    /// endregion

    /// region helper functions for Kotlin Poet
    internal fun KSType.getMethodName(resolver: Resolver): String {
        return when {
            isContract -> "getContract"
            isIntent -> "getIntent"
            isFragment(resolver) -> "getFragment"
            else -> ""
        }
    }

    internal fun KSType.getReturnType(resolver: Resolver): TypeName {
        return when {
            isContract -> toTypeName()
            isIntent -> toTypeName()
            isFragment(resolver) -> resolver.getClassDeclarationByName(FRAGMENT)!!.toClassName()
            else -> throw UnsupportedOperationException("Cannot determine return type for $this")
        }
    }
    /// endregion

}