package example.plugin.routing

import androidx.fragment.app.Fragment
import example.plugin.annotation.Route
import java.util.UUID

class HomeFragment : Fragment() {

    companion object {

        @Route
        fun newInstance() = HomeFragment().apply {

        }

        @Route("Personal")
        fun newInstance(id: UUID) = HomeFragment().apply {

        }
    }

    val default = HomeNavigationRoute { newInstance() }
    val example = PersonalNavigationRoute { id -> newInstance(id) }
}