pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        id("org.jetbrains.kotlin.jvm") version "1.9.0"
        id("com.android.library") version "8.3.0-beta02"
        id("org.jetbrains.kotlin.android") version "1.9.21"
        id("com.android.application") version "8.3.0-beta02"
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "android-codegen"
include(":annotation")
include(":processor")
include(":app")
