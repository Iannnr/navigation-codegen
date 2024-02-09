package example.plugin.routing

import android.app.Activity
import android.os.Bundle
import example.plugin.annotation.AutoRoute
import javax.inject.Inject

class AutoRoutingActivity: Activity() {

    @AutoRoute
    companion object

    @Inject
    lateinit var autoRoute: AutoRoutingNavigationRoute

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(
            autoRoute.getIntent(this)
        )
    }
}