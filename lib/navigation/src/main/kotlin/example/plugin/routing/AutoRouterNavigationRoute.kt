package example.plugin.routing

import androidx.fragment.app.Fragment

public fun interface AutoRouterNavigationRoute {
  public fun getFragment(): Fragment
}
