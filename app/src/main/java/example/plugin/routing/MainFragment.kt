package example.plugin.routing

import android.app.Fragment
import example.plugin.annotation.Route
import java.util.UUID

class MainFragment : Fragment() {

    companion object {

        @Route
        fun newInstance(): Fragment = MainFragment().apply {

        }

        @Route("ExampleFragment")
        fun newInstance(id: UUID): Fragment = MainFragment().apply {

        }
    }

    val default = MainFragmentNavigationRoute { newInstance() }
    val example = ExampleFragmentNavigationRoute { id -> newInstance(id) }
}