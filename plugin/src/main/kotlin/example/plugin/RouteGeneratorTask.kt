package example.plugin

import example.generator.Generator
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class RouteGeneratorTask : DefaultTask() {
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:InputDirectory
    abstract val inputDirectory: DirectoryProperty

    @TaskAction
    fun execute() {
        val outputDir = outputDirectory.get().asFile
        val inputDir = inputDirectory.get().asFile
        val generator = Generator()
        generator(inputDir, outputDir)
    }
}
