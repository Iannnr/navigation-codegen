package example.plugin.routing

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import example.plugin.annotation.Route

class ExampleContract: ActivityResultContract<Boolean, Boolean>() {

    companion object {

        @Route
        fun getContract(): ActivityResultContract<Boolean, Boolean> {
            return ExampleContract()
        }
    }

    override fun createIntent(context: Context, input: Boolean): Intent {
        return Intent()
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return true
    }

    val default = ExampleContractNavigationRoute { getContract() }
}