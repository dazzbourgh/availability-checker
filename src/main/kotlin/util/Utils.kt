package util

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import kotlin.time.Duration

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

fun WebDriver.findElementOrNull(by: By) = try {
    findElement(by)
} catch (_: NoSuchElementException) {
    null
}