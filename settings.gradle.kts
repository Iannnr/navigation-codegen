pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        resolutionStrategy {
            eachPlugin {
                val id = requested.id.id
                when {
                    id.startsWith("com.android.") -> useModule("com.android.tools.build:gradle:8.1.0-beta05")
                    id.startsWith("org.jetbrains.kotlin.") -> useVersion("1.9.21")
                }
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "android-codegen"
includeBuild("generator")
includeBuild("plugin")
