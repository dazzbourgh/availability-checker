package util

import arrow.core.left
import arrow.core.raise.Raise
import arrow.core.right
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import kotlin.time.Duration

fun interface Waiter {
    suspend fun wait(delay: Duration)
}

context(LogError, Waiter)
tailrec suspend fun <T> retry(
    times: Int,
    delay: Duration,
    condition: (T) -> Boolean,
    default: T,
    block: suspend () -> T
): T =
    if (times == 0) default
    else {
        val result = try {
            block()
        } catch (e: Exception) {
            error(e.message ?: "Unknown error")
            null
        }?.let {
            if (condition(it)) it else null
        }
        if (result != null) result else {
            wait(delay)
            retry(times - 1, delay, condition, default, block)
        }
    }

context(Raise<E>)
fun <E> WebDriver.findByXpath(xpathExpression: String, error: E): WebElement =
    runCatching { findElement(By.xpath(xpathExpression)) }
        .fold(
            { it.right() },
            { error.left() }
        )
        .bind()

fun WebDriver.findElementOrNull(by: By) = try {
    findElement(by)
} catch (_: NoSuchElementException) {
    null
}