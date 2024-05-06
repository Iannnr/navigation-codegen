package example.plugin.routing

import android.content.Context
import android.content.Intent

public fun interface AutoRoutingNavigationRoute {
  public fun getIntent(context: Context): Intent
}
