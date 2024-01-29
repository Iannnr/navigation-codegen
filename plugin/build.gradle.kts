plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "example.plugin"

gradlePlugin {
    plugins {
        create("plugin") {
            id = "example.plugin"
            implementationClass = "example.plugin.NavigationRoutesPlugin"
        }
    }
}

dependencies {
    implementation("example.generator:generator")
    // switch to gradle-api when it is complete enough
    compileOnly("com.android.tools.build:gradle:8.1.0-beta05")
}
