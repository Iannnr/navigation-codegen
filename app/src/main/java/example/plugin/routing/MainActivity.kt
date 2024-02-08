package example.plugin.routing

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import example.plugin.annotation.Route
import java.util.UUID

class MainActivity : ComponentActivity() {

    companion object {
        @Route
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }

        @Route("Example")
        fun getIntent(context: Context, id: UUID): Intent {
            return Intent(context, MainActivity::class.java)
                .putExtra("id", id)
        }
    }

    val default = MainNavigationRoute { context -> getIntent(context) }
    val example = ExampleNavigationRoute { context, id -> getIntent(context, id) }
}