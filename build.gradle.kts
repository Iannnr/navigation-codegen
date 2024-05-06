plugins {
    id("com.android.library")
    kotlin("android")
    id("com.google.devtools.ksp") version libs.versions.ksp.get() apply false
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 23
    }

    namespace = "example.plugin"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = libs.versions.kotlin.get()))
        classpath(kotlin("serialization", version = libs.versions.kotlin.get()))
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation(libs.fragment)

    implementation(project(":annotation"))

    testImplementation("junit:junit:4.13.2")
}
