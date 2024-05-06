package plugin.extension

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import java.io.Serializable
import javax.inject.Inject

open class RoutingExtension @Inject constructor(
    objects: ObjectFactory
) {
    private val navigationOptions = objects.property<NavigationOptions>()

    fun getNavigationOptions(): Property<NavigationOptions> {
        navigationOptions.convention(NavigationOptions())
        return navigationOptions
    }

    fun options(action: Action<NavigationOptions>? = null) {
        val options = NavigationOptions()
        action?.execute(options)
        navigationOptions.set(options)
    }
}

open class NavigationOptions : Serializable {
    var outputModule: String = "lib/navigation"
    var outputNamespace: String = "example.plugin.routing"
    var routeSuffix: String = "Route.kt"
    var flavours: List<String> = listOf("debug", "release")
}