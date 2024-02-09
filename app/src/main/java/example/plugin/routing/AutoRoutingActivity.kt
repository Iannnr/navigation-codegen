package example.plugin.routing

import android.app.Activity
import androidx.fragment.app.Fragment
import example.plugin.annotation.AutoRoute
import javax.inject.Inject

@AutoRoute
class AutoRoutingActivity: Activity() {

    companion object {}

    @Inject
    lateinit var autoRoute: AutoRoutingNavigationRoute

}

@AutoRoute
class AutoRouterFragment: Fragment() {

    companion object {}

}