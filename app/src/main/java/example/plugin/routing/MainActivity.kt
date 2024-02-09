package example.plugin.routing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import example.plugin.annotation.NavigationRoute
import java.util.UUID
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    // automatically created by KSP processor and injectable

    //inject the HomeFragment route
    @Inject
    lateinit var home: HomeNavigationRoute

    // inject the overridden Personal Navigation Route for HomeFragment
    @Inject
    lateinit var personal: PersonalNavigationRoute

    // inject the default activity result contract navigation route
    @Inject
    lateinit var contract: ContractNavigationRoute

    // inject the overridden activity result contract navigation route
    @Inject
    lateinit var secondary: SecondaryNavigationRoute

    companion object {
        @NavigationRoute
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }

        @NavigationRoute("Example")
        fun getIntent(context: Context, id: UUID): Intent {
            return Intent(context, MainActivity::class.java)
                .putExtra("id", id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerForActivityResult(secondary.getContract()) { result ->

        }

        registerForActivityResult(contract.getContract()) { result ->

        }
    }
}