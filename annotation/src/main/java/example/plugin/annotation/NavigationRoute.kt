package example.plugin.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class NavigationRoute(
    val overrideRouteName: String = ""
)