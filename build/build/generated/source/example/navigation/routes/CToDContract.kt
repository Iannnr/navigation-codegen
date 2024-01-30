package navigation.routes

import android.content.Context
import androidx.activity.result.contract.ActivityResultContract

public fun interface CToDContract {
  public fun getContract(context: Context): ActivityResultContract<*, *>
}
