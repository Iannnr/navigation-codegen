## CodeGen Navigation Routing
_Rough POC for using [Kotlin Poet](https://square.github.io/kotlinpoet/) to codegen interfaces for Navigation Routes with annotation processing_

### **This will generate the SAM interfaces only, as part of the build process**
---
* Provides annotations to setup specific navigation routes, or auto routing when no additional arguments are required, which generates an interface, an implementation and a dagger module binding, then moves the interfaces into a shared module by location & namespace to be shared across different gradle module
* Determines the method name passed on the return type
* Uses return type reference exactly, so not need for reflection of acceptable types
* Creates a Dagger module for binding implementations to the respective navigation interfaces
* Provides an AutoRoute annotation to create a default route interface and interface to reference the current class
* Provides a convention plugin for navigation routing to provide options ie where to place interfaces, route name suffix, etc.

### Annotations: 
* [@NavigationRoute](https://github.com/Iannnr/navigation-codegen/blob/master/annotation/src/main/java/example/plugin/annotation/NavigationRoute.kt)
* [@AutoRoute](https://github.com/Iannnr/navigation-codegen/blob/master/annotation/src/main/java/example/plugin/annotation/AutoRoute.kt)

### Processors: 
* [AutoRouteProcessor](https://github.com/Iannnr/navigation-codegen/blob/master/processor/src/main/java/example/plugin/processor/autorouting/AutoRouteProcessor.kt)
* [NavigationRouteProcessor](https://github.com/Iannnr/navigation-codegen/blob/master/processor/src/main/java/example/plugin/processor/routing/RouteProcessor.kt)

### Generators: 
* [RouteGenerator](https://github.com/Iannnr/navigation-codegen/blob/master/processor/src/main/java/example/plugin/processor/routing/RouteGenerator.kt)
* [DaggerModuleGenerator](https://github.com/Iannnr/navigation-codegen/blob/master/processor/src/main/java/example/plugin/processor/routing/DaggerModuleGenerator.kt)
* [NavigationImplementationGenerator](https://github.com/Iannnr/navigation-codegen/blob/master/processor/src/main/java/example/plugin/processor/routing/NavigationImplementationGenerator.kt)
* [NavigationInterfaceGenerator](https://github.com/Iannnr/navigation-codegen/blob/master/processor/src/main/java/example/plugin/processor/routing/NavigationInterfaceGenerator.kt)

Examples:
- [Activity](https://github.com/Iannnr/navigation-codegen/blob/master/app/src/main/java/example/plugin/routing/MainActivity.kt)
- [Fragment](https://github.com/Iannnr/navigation-codegen/blob/master/app/src/main/java/example/plugin/routing/HomeFragment.kt) 
- [Contract](https://github.com/Iannnr/navigation-codegen/blob/master/app/src/main/java/example/plugin/routing/ActivityContract.kt)


TODO:
- Detect when more than one method is going to be generated from a class, and change from a SAM interface to one which has methods for all routes
- Cleanup KPoet classes
- Reduce, simplify, document
