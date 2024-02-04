plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.9.21-1.0.16"
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}