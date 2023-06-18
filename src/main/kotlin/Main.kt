import arrow.fx.coroutines.resource
import arrow.fx.coroutines.resourceScope
import checker.Credentials
import checker.Failure
import checker.ItalianConsulateChecker
import checker.Success
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.types.ChatId
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import notifier.telegram.TelegramNotifier
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import util.ConsoleLog
import util.Waiter
import kotlin.time.Duration.Companion.seconds

fun main() = runBlocking {
    val timeout = 90.seconds
    val email = System.getenv("email")
    val password = System.getenv("password")
    val token = System.getenv("telegram_token")
    val chatIdentifier = System.getenv("telegram_chat_id").toLong()

    val driverFactory = {
        ChromeDriver(ChromeOptions().apply {
            addArguments("--headless=new")
        })
    }
    val credentials = Credentials(email, password)
    val waiter = Waiter { delay(it) }
    val chatId = ChatId(chatIdentifier)

    val chromeDriverResource = resource(driverFactory) { driver, _ ->
        driver.close()
        driver.quit()
    }
    val botResource = resource({ telegramBot(token) }) { bot, _ -> bot.close() }
    val notifierResource = resource { TelegramNotifier(botResource.bind(), chatId) }
    val checkerResource = resource {
        ItalianConsulateChecker(
            chromeDriverResource.bind(),
            waiter,
            ConsoleLog,
            credentials
        )
    }
    resourceScope {
        val notifier = notifierResource.bind()
        val checker = checkerResource.bind()
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
}
