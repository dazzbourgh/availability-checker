package checker

import arrow.core.raise.either
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import util.Log
import util.Waiter
import util.findByXpath
import util.retry
import kotlin.time.Duration.Companion.seconds

private sealed class Error
private class ButtonNotFound(val name: String) : Error()
private class FieldNotFound(val name: String) : Error()
private object PopUpNotFound : Error()

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

    override suspend fun check(): Boolean = with(waiter) {
        with(log) {
            val driver = chromeDriverFactory()
            driver.get(startingUrl)
            val result = either {

                val email = driver.findByXpath(emailXPath, FieldNotFound("email"))
                val password = driver.findByXpath(passwordXPath, FieldNotFound("password"))
                val submitButton = driver.findByXpath(loginButtonXPath, ButtonNotFound("sign in"))

                email.sendKeys(credentials.email)
                wait(1.seconds)
                password.sendKeys(credentials.password)
                wait(1.seconds)
                submitButton.click()

                wait(5.seconds)

                retry(2, 10.seconds, PopUpNotFound) {
                    driver.findElement(By.cssSelector(popupSelector))
                }

                Unit
            }
            result.fold({
                when (it) {
                    is ButtonNotFound -> {
                        error("Button not found: ${it.name}")
                        false
                    }

                    is FieldNotFound -> {
                        error("Field not found: ${it.name}")
                        false
                    }

                    PopUpNotFound -> true
                }
            }, {
                info("No appointments available")
                false
            })
        }
    }

    override val name: String = "Italian tourist visa appointment in Houston's consulate"
    override val startingUrl: String = "https://prenotami.esteri.it/Services/Booking/1250"
}
