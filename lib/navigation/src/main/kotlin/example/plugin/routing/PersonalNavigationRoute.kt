package example.plugin.routing

import androidx.fragment.app.Fragment
import java.util.UUID

public fun interface PersonalNavigationRoute {
  public fun getFragment(id: UUID): Fragment
}
