package navigation.routes

import android.content.Context
import android.content.Intent

public fun interface AToBFragment {
  public fun getFragment(context: Context): Intent
}
