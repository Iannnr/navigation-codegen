## CodeGen Navigation Routing
_Rough POC for using [Kotlin Poet](https://square.github.io/kotlinpoet/) to generate Navigation Routes as part of a Gradle Plugin_

**This will generate the SAM interfaces only**
---
### Config file, and examples of routes defined in [routes.json](https://github.com/Iannnr/navigation-codegen/blob/master/data/routes.json)
### Output examples are in [build output directory](https://github.com/Iannnr/navigation-codegen/tree/master/build/build/generated/source/example/navigation/routes)
### Acceptable types & class structure is in [Route.kt](https://github.com/Iannnr/navigation-codegen/blob/master/generator/src/main/kotlin/example/generator/config/Route.kt)
### Codegen is in the [Generator](https://github.com/Iannnr/navigation-codegen/blob/master/generator/src/main/kotlin/example/generator/Generator.kt)
