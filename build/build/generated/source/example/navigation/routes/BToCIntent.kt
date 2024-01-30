package navigation.routes

import android.content.Context
import androidx.fragment.app.Fragment
import java.util.UUID
import kotlin.Boolean

public fun interface BToCIntent {
  public fun getIntent(
    context: Context,
    id: UUID,
    hasFlag: Boolean,
  ): Fragment
}
