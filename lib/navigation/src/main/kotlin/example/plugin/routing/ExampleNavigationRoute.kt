package example.plugin.routing

import android.content.Context
import android.content.Intent
import java.util.UUID

public fun interface ExampleNavigationRoute {
  public fun getIntent(context: Context, id: UUID): Intent
}
