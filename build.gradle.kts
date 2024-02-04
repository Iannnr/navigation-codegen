plugins {
    id("com.android.library")
    kotlin("android")
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
    repositories { mavenCentral() }

    dependencies {
        val kotlinVersion = "1.9.21"
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath(kotlin("serialization", version = kotlinVersion))
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    implementation(project(":annotation"))

    testImplementation("junit:junit:4.13.2")
}
