plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "2.0.0-RC2-1.0.20"
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}