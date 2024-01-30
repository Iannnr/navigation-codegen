package example.generator.config

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val from: String,
    val to: String,
    val type: Type,
    val arguments: Map<String, ArgumentTypes>?,
)

@Serializable
enum class Type(val signature: String, private val packageName: String) {
    FRAGMENT("Intent", "androidx.fragment.app.Fragment"),
    INTENT("Fragment", "android.content.Intent"),
    CONTRACT("Contract", "androidx.activity.result.contract.ActivityResultContract");

    // adds star projection for ActivityResultContract<*, *>
    val returnType: TypeName
        get() = if (this == CONTRACT) {
            packageName.asClassName.parameterizedBy(STAR, STAR)
        } else {
            packageName.asClassName
        }
}

@Serializable
enum class ArgumentTypes(packageName: String) {
    BOOLEAN("kotlin.Boolean"),
    STRING("kotlin.String"),
    INTEGER("kotlin.Integer"),
    LONG("kotlin.Long"),
    UUID("java.util.UUID");

    val className = packageName.asClassName
}

private val String.asClassName: ClassName
    get() = ClassName(substringBeforeLast("."), substringAfterLast("."))
