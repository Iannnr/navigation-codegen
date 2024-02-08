package example.plugin.processor

import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.ksp.toClassName

object ClassNames {

    object ClassPaths {
        internal const val FRAGMENT = "androidx.fragment.app.Fragment"
        internal const val INTENT = "android.content.Intent"
        internal const val CONTRACT = "androidx.activity.result.contract.ActivityResultContract"
    }

    internal val context = ClassName("android.content", "Context")
    private val intent = ClassName("android.content", "Intent")
    private val contract = ClassName("androidx.activity.result.contract", "ActivityResultContract").parameterizedBy(STAR, STAR)

    internal val KSType.isContext: Boolean
        get() = toClassName() == context

    internal val KSType.isIntent: Boolean
        get() = toClassName() == intent

    internal val KSType.isContract: Boolean
        get() = toClassName().parameterizedBy(STAR, STAR) == contract

}