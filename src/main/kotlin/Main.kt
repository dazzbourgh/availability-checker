import checker.Credentials
import checker.ItalianConsulateChecker
import dev.inmo.tgbotapi.extensions.api.telegramBot
import dev.inmo.tgbotapi.types.ChatId
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import notifier.Notifier
import notifier.telegram.TelegramNotifier
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import util.ConsoleLog
import util.Waiter

fun main() = runBlocking {
    val email = System.getenv("email")
    val password = System.getenv("password")
    val token = System.getenv("telegram_token")
    val chatIdentifier = System.getenv("telegram_chat_id").toLong()

    val chromeDriverFactory: () -> WebDriver = {
        ChromeDriver(ChromeOptions().apply {
            addArguments("--headless=new")
        })
    }
    val waiter = Waiter { delay(it) }
    val credentials = Credentials(email, password)
    val bot = telegramBot(token)
    val chatId = ChatId(chatIdentifier)
    val notifier: Notifier = TelegramNotifier(bot, chatId)
    val checker = ItalianConsulateChecker(chromeDriverFactory, waiter, ConsoleLog, credentials)
    val notification = "${checker.name} is available at: ${checker.startingUrl}"

    if (checker.check()) notifier.notify(notification)
}
