package example.plugin.routing

import android.content.Context
import android.content.Intent

public fun interface MainNavigationRoute {
  public fun getIntent(context: Context): Intent
}
