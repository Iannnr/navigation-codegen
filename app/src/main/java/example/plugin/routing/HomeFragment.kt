package example.plugin.routing

import androidx.fragment.app.Fragment
import example.plugin.annotation.NavigationRoute
import java.util.UUID
import javax.inject.Inject

class HomeFragment : Fragment() {

    @Inject
    lateinit var home: MainNavigationRoute

    @Inject
    lateinit var personal: ExampleNavigationRoute

    companion object {

        @NavigationRoute
        fun newInstance() = HomeFragment().apply {

        }

        @NavigationRoute("Personal")
        fun newInstance(id: UUID) = HomeFragment().apply {

        }
    }
}