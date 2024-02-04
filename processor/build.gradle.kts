plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.9.21-1.0.16"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

dependencies {
    implementation(project(":annotation"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.21-1.0.16")
    implementation("com.squareup:kotlinpoet:1.16.0")
    implementation("com.squareup:kotlinpoet-ksp:1.14.2")
}