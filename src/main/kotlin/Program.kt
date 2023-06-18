import checker.Failure
import checker.Success
import checker.WebsiteChecker
import kotlinx.coroutines.withTimeout
import notifier.Notifier
import util.ConsoleLog
import kotlin.time.Duration

class Program(val checker: WebsiteChecker, val notifier: Notifier)

suspend fun runProgram(program: Program, timeout: Duration) {
    val checker = program.checker
    val notifier = program.notifier
    val notification = "${checker.name} is available at: ${checker.startingUrl}"

    runCatching { withTimeout(timeout) { checker.check() } }
        .fold(
            onSuccess = { result ->
                when (result) {
                    is Failure -> ConsoleLog.info(result.message)
                    Success -> notifier.notify(notification)
                }
            },
            onFailure = {
                notifier.notify("Error occurred: $it")
            }
        )
}