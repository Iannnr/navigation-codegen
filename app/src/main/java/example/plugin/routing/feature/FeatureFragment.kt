package example.plugin.routing.feature

import androidx.fragment.app.Fragment
import example.plugin.annotation.NavigationRoute
import javax.inject.Inject

class FeatureFragment : Fragment() {

    @Inject
    lateinit var home: FeatureNavigationRoute

    companion object {

        @NavigationRoute
        fun newInstance() = FeatureFragment().apply {

        }
    }
}