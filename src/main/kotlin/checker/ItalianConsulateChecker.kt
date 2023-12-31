package checker

import arrow.fx.coroutines.parZip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import util.Log
import util.Waiter
import util.findElementOrNull
import util.retry
import kotlin.time.Duration.Companion.seconds

data class Credentials(val email: String, val password: String)

class ItalianConsulateChecker(
    private val driver: WebDriver,
    private val waiter: Waiter,
    private val log: Log,
    private val credentials: Credentials
) : WebsiteChecker {
    companion object {
        private const val emailXPath = "//*[@id=\"login-email\"]"
        private const val passwordXPath = "//*[@id=\"login-password\"]"
        private const val loginButtonXPath = "//*[@id=\"login-form\"]/button"
        private const val popupSelector = "div.jconfirm-content"
        private const val tableId = "dataTableServices"
    }

    override suspend fun check(): CheckResult = coroutineScope {
        with(waiter) {
            with(log) {
                driver.get(startingUrl)

                val email =
                    driver.findByXPath(emailXPath) ?: return@coroutineScope Failure("email field not found")
                val password =
                    driver.findByXPath(passwordXPath) ?: return@coroutineScope Failure("password field not found")
                val submitButton =
                    driver.findByXPath(loginButtonXPath) ?: return@coroutineScope Failure("sign in button not found")

                email.sendKeys(credentials.email)
                wait(1.seconds)
                password.sendKeys(credentials.password)
                wait(1.seconds)
                submitButton.click()
                wait(5.seconds)

                parZip(
                    { driver.findWithRetry(By.cssSelector(popupSelector)) },
                    { driver.findWithRetry(By.id(tableId)) }
                ) { popup, table ->
                    if (popup == null && table == null) Success else Failure("no appointments available")
                }
            }
        }
    }

    context(Log, Waiter, CoroutineScope)
    private suspend fun WebDriver.findWithRetry(by: By) =
        retry(2, 10.seconds, { it != null }, null) {
            driver.findElementOrNull(by)
        }

    private fun WebDriver.findByXPath(xpath: String) =
        findElementOrNull(By.xpath(xpath))

    override val name: String = "Italian tourist visa appointment in Houston's consulate"
    override val startingUrl: String = "https://prenotami.esteri.it/Services/Booking/1250"
}
