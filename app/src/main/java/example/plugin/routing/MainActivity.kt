package example.plugin.routing

import android.app.Fragment
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import example.plugin.annotation.Route
import java.util.UUID

class MainActivity : ComponentActivity() {

    companion object {
        @Route
        fun getIntent(context: Context, id: UUID): Intent {
            return Intent(context, MainActivity::class.java)
                .putExtra("id", id)
        }

        @Route("MainFragment")
        fun getFragment(): Fragment {
            return Fragment()
        }

        @Route("MainContract")
        fun getContract(): ActivityResultContract<Boolean, Boolean> {
            return object: ActivityResultContract<Boolean, Boolean>() {
                override fun createIntent(context: Context, input: Boolean): Intent {
                    return Intent()
                }

                override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
                    return true
                }
            }
        }
    }

    // examples of generated code
    val intentRoute = MainActivityNavigationRoute { context, id -> getIntent(context, id) }
    val fragmentRoute = MainFragmentNavigationRoute { getFragment() }
    val contractRoute = MainContractNavigationRoute { getContract() }
}