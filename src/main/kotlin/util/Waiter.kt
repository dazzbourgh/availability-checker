package util

import kotlin.time.Duration

fun interface Waiter {
    suspend fun wait(delay: Duration)
}
