package example.plugin.routing

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import example.plugin.annotation.NavigationRoute
import java.util.UUID

class MainActivity : ComponentActivity() {

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

    val default = MainNavigationRoute { context -> getIntent(context) }
    val example = ExampleNavigationRoute { context, id -> getIntent(context, id) }
}