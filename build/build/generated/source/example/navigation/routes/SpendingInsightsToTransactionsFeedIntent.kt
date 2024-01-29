package navigation.routes

import android.content.Context
import androidx.fragment.app.Fragment
import java.util.UUID
import kotlin.Boolean

public fun interface SpendingInsightsToTransactionsFeedIntent {
  public fun getIntent(
    context: Context,
    accountId: UUID,
    hasFlag: Boolean,
  ): Fragment
}
