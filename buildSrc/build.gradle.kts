plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("navigation.route") {
            id = "navigation.route"
            implementationClass = "plugin.NavigationRoutePlugin"
        }
    }
}

dependencies {
    implementation(libs.agp)
    implementation(libs.gradle.plugin)

    testImplementation(libs.junit)
}
