package navigation.routes

import android.content.Context
import android.content.Intent

public fun interface AccountDetailsToForgotPasswordFragment {
  public fun getFragment(context: Context): Intent
}
