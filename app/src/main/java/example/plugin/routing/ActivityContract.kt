package example.plugin.routing

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import example.plugin.annotation.Route

class ActivityContract: ActivityResultContract<Boolean, Boolean>() {

    @Route("doesNothing")
    fun getContract() = ActivityContract()

    companion object {

        @Route
        fun getContract(): ActivityResultContract<Boolean, Boolean> {
            return ActivityContract()
        }

        @Route("Secondary")
        fun getSecondaryContract(): ActivityResultContract<Boolean, Boolean> {
            return ActivityContract()
        }
    }

    override fun createIntent(context: Context, input: Boolean): Intent {
        return Intent()
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        return true
    }

    val default = ContractNavigationRoute { getContract() }
    val example = SecondaryNavigationRoute { getContract() }
}