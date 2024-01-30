package navigation.routes

import android.content.Context
import androidx.fragment.app.Fragment
import java.util.UUID
import kotlin.Boolean
import kotlin.Long
import kotlin.String

public fun interface AToBIntent {
  public fun getIntent(
    context: Context,
    id: UUID,
    number: Long,
    showEverything: Boolean,
    content: String,
  ): Fragment
}
