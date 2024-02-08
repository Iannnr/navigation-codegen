plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version libs.versions.ksp.get()
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

    implementation(libs.ksp)
    implementation(libs.poet)
    implementation(libs.poet.ksp)
}