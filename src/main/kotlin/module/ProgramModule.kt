package module

import arrow.fx.coroutines.Resource
import checker.WebsiteChecker
import notifier.Notifier
import kotlin.time.Duration

interface ProgramModule {
    val timeout: Duration
    val checkerResource: Resource<WebsiteChecker>
    val notifierResource: Resource<Notifier>
}
