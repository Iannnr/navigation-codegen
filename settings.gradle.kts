pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        id("org.jetbrains.kotlin.jvm") version "2.0.0-RC2"
        id("com.android.library") version "8.4.0"
        id("org.jetbrains.kotlin.android") version "2.0.0-RC2"
        id("com.android.application") version "8.4.0"
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
include(":lib:navigation")
