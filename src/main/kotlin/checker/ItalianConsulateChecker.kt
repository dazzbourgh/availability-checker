package checker

import arrow.core.raise.either
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import util.*
import kotlin.time.Duration.Companion.seconds

data class Credentials(val email: String, val password: String)

class ItalianConsulateChecker(
    private val chromeDriverFactory: () -> WebDriver,
    private val waiter: Waiter,
    private val log: Log,
    private val credentials: Credentials
) : WebsiteChecker {
    companion object {
        private const val emailXPath = "//*[@id=\"login-email\"]"
        private const val passwordXPath = "//*[@id=\"login-password\"]"
        private const val loginButtonXPath = "//*[@id=\"login-form\"]/button"
        private const val popupSelector = "div.jconfirm-content"
    }

    override suspend fun check(): CheckResult = with(waiter) {
        with(log) {
            either {
                val driver = chromeDriverFactory()
                driver.get(startingUrl)
                val email = driver.findByXpath(emailXPath, Failure("email field not found"))
                val password = driver.findByXpath(passwordXPath, Failure("password field not found"))
                val submitButton = driver.findByXpath(loginButtonXPath, Failure("sign in button not found"))

                email.sendKeys(credentials.email)
                wait(1.seconds)
                password.sendKeys(credentials.password)
                wait(1.seconds)
                submitButton.click()
                wait(5.seconds)

                val result = retry(2, 10.seconds, { it != null }, null) {
                    driver.findElementOrNull(By.cssSelector(popupSelector))
                }
                driver.close()
                if (result == null) Success else raise(Failure("no appointments available"))
            }.fold({ it }, { it })
        }
    }

    override val name: String = "Italian tourist visa appointment in Houston's consulate"
    override val startingUrl: String = "https://prenotami.esteri.it/Services/Booking/1250"
}
