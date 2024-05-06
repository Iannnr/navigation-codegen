package example.plugin.routing.feature

import androidx.fragment.app.Fragment

public fun interface FeatureNavigationRoute {
  public fun getFragment(): Fragment
}
