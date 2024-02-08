## CodeGen Navigation Routing
_Rough POC for using [Kotlin Poet](https://square.github.io/kotlinpoet/) to codegen interfaces for Navigation Routes with annotation processing_

### **This will generate the SAM interfaces only, as part of the build process**
---
Currently creates an interface in the same directory as the file, but should change to a common location, allows for a navigation route name override, or defaults to the class name + NavigationRoute
Determines the method name passed on the return type
Uses return type reference exactly, so not need for reflection of acceptable types


Annotation class: [@Route](https://github.com/Iannnr/navigation-codegen/blob/master/annotation/src/main/java/example/plugin/annotation/Route.kt)

Processor: [RouteProcessor](https://github.com/Iannnr/navigation-codegen/blob/master/processor/src/main/java/example/plugin/processor/RouteProcessor.kt)
- RouteValidator should have some rules about can/can't be annotated and used for routes, needs to handle Fragments better

Generator: [RouteGenerator](https://github.com/Iannnr/navigation-codegen/blob/master/processor/src/main/java/example/plugin/processor/RouteGenerator.kt)
- Definitely needs tests and some more edge cases handled.

Examples:
- [Activity](https://github.com/Iannnr/navigation-codegen/blob/master/app/src/main/java/example/plugin/routing/MainActivity.kt)
- [Fragment](https://github.com/Iannnr/navigation-codegen/blob/master/app/src/main/java/example/plugin/routing/HomeFragment.kt) 
- [Contract](https://github.com/Iannnr/navigation-codegen/blob/master/app/src/main/java/example/plugin/routing/ActivityContract.kt)

TODO:
- Detect when more than one method is going to be generated from a class, and change from a SAM interface to one which has methods for all routes
- Create Dagger Module which auto binds all generated implementation
- Create annotation which auto generates the default "getIntent/newInstance/getContract" for the root Route class
