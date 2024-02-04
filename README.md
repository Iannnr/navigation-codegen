## CodeGen Navigation Routing
_Rough POC for using [Kotlin Poet](https://square.github.io/kotlinpoet/) to codegen interfaces for Navigation Routes with annotation processing_

### **This will generate the SAM interfaces only, as part of the build process**
---
Currently creates an interface in the same directory as the file, but should change to a common location, allows for a navigation route name override, or defaults to the class name + NavigationRoute
Determines the method name passed on the return type
Uses return type reference exactly, so not need for reflection of acceptable types


Annotation class: [@Route.kt](https://github.com/Iannnr/navigation-codegen/blob/master/annotation/src/main/java/example/plugin/annotation/Route.kt)

Processor: [RouteProcessor](https://github.com/Iannnr/navigation-codegen/blob/master/processor/src/main/java/example/plugin/processor/RouteProcessor.kt)
- RouteValidator should have some rules about can/can't be annotated and used for routes

Generator: [RouteGenerator](https://github.com/Iannnr/navigation-codegen/blob/master/processor/src/main/java/example/plugin/processor/RouteGenerator.kt)
- Definitely needs tests and some more edge cases handled.

Examples:
- Input / annotated methods - [MainActivity](https://github.com/Iannnr/navigation-codegen/blob/master/app/src/main/java/example/plugin/routing/MainActivity.kt#L14-L36)
- Output / usage of generated functional SAM interfaces - [MainActivity](https://github.com/Iannnr/navigation-codegen/blob/master/app/src/main/java/example/plugin/routing/MainActivity.kt#L40-L42)

TODO:
- Create codegen for implementation which references the method, maybe in the same file ie.
```
class MainActivityNavigationRouteImpl @Inject constructor: MainActivityNavigationRoute {
  fun getIntent(context: Context) = MainActivity.getIntent(context)
}
```
- Remove relevant suffixes from class names to make `MainNavigationRoute` so it doesn't need to indicate activity/fragment/contract etc, only the method needs to
