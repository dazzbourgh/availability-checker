package module

import arrow.fx.coroutines.resource
import checker.Credentials
import checker.ItalianConsulateChecker
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.types.ChatId
import kotlinx.coroutines.delay
import notifier.telegram.TelegramNotifier
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import util.ConsoleLog
import util.Waiter
import kotlin.time.Duration.Companion.minutes

object ItalyConsulateModule : ProgramModule {
    private val email = System.getenv("email")
    private val password = System.getenv("password")
    private val token = System.getenv("telegram_token")
    private val chatIdentifier = System.getenv("telegram_chat_id").toLong()
    private val driverFactory = {
        ChromeDriver(ChromeOptions().apply {
            addArguments("--headless=new")
        })
    }
    private val credentials = Credentials(email, password)
    private val waiter = Waiter { delay(it) }
    private val chatId = ChatId(chatIdentifier)
    private val chromeDriverResource = resource(driverFactory) { driver, _ ->
        driver.close()
        driver.quit()
    }
    private val botResource = resource({ telegramBot(token) }) { bot, _ -> bot.close() }

    override val timeout = 1.minutes
    override val checkerResource = resource {
        ItalianConsulateChecker(
            chromeDriverResource.bind(),
            waiter,
            ConsoleLog,
            credentials
        )
    }
    override val notifierResource = resource { TelegramNotifier(botResource.bind(), chatId) }
}
