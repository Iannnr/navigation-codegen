package example.plugin.routing

import androidx.activity.result.contract.ActivityResultContract
import kotlin.Boolean

public fun interface ContractNavigationRoute {
  public fun getContract(): ActivityResultContract<Boolean, Boolean>
}
