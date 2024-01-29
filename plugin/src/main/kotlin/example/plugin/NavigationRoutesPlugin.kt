package example.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering
import java.io.File

class NavigationRoutesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val outputDir = target.layout.buildDirectory.dir("build/generated/source/example")
        val inputDir = File(target.project.projectDir, "/data")

        val exampleGenerator by target.tasks.registering(RouteGeneratorTask::class) {
            inputDirectory.set(inputDir)
            outputDirectory.set(outputDir)
        }

        target.pluginManager.withPlugin("com.android.application") {
            val android = target.extensions["android"] as AppExtension
            android.applicationVariants.all {
                registerJavaGeneratingTask(exampleGenerator, inputDir, outputDir.get().asFile)
            }
        }

        target.pluginManager.withPlugin("com.android.library") {
            val android = target.extensions["android"] as LibraryExtension
            android.libraryVariants.all {
                registerJavaGeneratingTask(exampleGenerator, inputDir, outputDir.get().asFile)
            }
        }
    }
}
