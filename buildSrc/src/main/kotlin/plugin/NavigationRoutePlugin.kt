package plugin

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import plugin.extension.RoutingExtension
import java.io.File

class NavigationRoutePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.kotlinExtension.jvmToolchain(17)

        target.dependencies {
            add("implementation", project(":lib:navigation"))
            add("implementation", project(":annotation"))
            add("ksp", project(":processor"))
        }

        val components = target.extensions.getByType(AndroidComponentsExtension::class.java)
        val extension = target.extensions.create<RoutingExtension>("navigationRouting")

        components.onVariants { type ->
            val buildTypeName = type.name.uppercaseFirstChar()
            val options = extension.getNavigationOptions().get()
            val namespace = options.outputNamespace.replace(".", "/")
            val routeSuffix = options.routeSuffix
            val outputModule = options.outputModule

            if (options.flavours.contains(type.name)) {
                target.tasks.register("generate${buildTypeName}NavigationRoutes") {
                    doLast {
                        val interfaces = File(target.buildDir, "generated/ksp/${type.name}") // keep lowercase
                            .listFiles()
                            .orEmpty()
                            .flatMap { it.walkTopDown().toList() }
                            .filter { it.name.contains(routeSuffix, true) }

                        if (interfaces.isEmpty()) {
                            target.logger.error("No navigation route interfaces")
                        } else {
                            // root folder for all navigation route interfaces
                            val root = File(target.rootProject.projectDir, "$outputModule/src/main/kotlin/$namespace")

                            interfaces.onEach {
                                val replace = File(root, it.path.substringAfter(root.name)) // path might include some extra subdirectories
                                println("file $replace : exists ${replace.exists()}")

                                // delete if the replacement location exists, or ensure the folder tree structure exists
                                if (replace.exists()) {
                                    replace.delete()
                                } else {
                                    replace.parentFile.mkdirs()
                                }

                                // move interface from app/lib module into lib:navigation
                                if (it.renameTo(replace)) {
                                    it.delete()
                                }
                            }
                        }
                    }
                }

                // make sure it runs after the compile-Kotlin task
                target.afterEvaluate {
                    println("afterEvaluate")
                    tasks.getByName("compile${buildTypeName}Kotlin")
                        .finalizedBy("generate${buildTypeName}NavigationRoutes")
                }
            }
        }
    }
}