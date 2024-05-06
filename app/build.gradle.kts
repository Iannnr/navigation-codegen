plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp")
    id("navigation.route")
}

android {
    namespace = "example.plugin.routing"
    compileSdk = 34

    defaultConfig {
        applicationId = "example.plugin.routing"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    sourceSets.configureEach {
        kotlin.srcDir("$buildDir/generated/ksp/$name/kotlin/")
    }
}

dependencies {
    implementation(libs.activity)
    implementation(libs.fragment)

    // transitive @Inject dependency
    implementation(libs.dagger.core)

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
}

navigationRouting {
    options {
        outputModule = "lib/navigation"
        outputNamespace = "example.plugin.routing"
        flavours = listOf("debug", "release")
        routeSuffix = "Route.kt"
    }
}