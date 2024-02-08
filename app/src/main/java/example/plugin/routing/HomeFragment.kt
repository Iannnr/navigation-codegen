package example.plugin.routing

import androidx.fragment.app.Fragment
import example.plugin.annotation.NavigationRoute
import java.util.UUID

class HomeFragment : Fragment() {

    companion object {

        @NavigationRoute
        fun newInstance() = HomeFragment().apply {

        }

        @NavigationRoute("Personal")
        fun newInstance(id: UUID) = HomeFragment().apply {

        }
    }

    val default = HomeNavigationRoute { newInstance() }
    val example = PersonalNavigationRoute { id -> newInstance(id) }
}