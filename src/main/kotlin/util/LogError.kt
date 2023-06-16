package util

import java.time.LocalDateTime

fun interface LogError {
    suspend fun error(msg: String)
}

object ConsoleLogError : LogError {
    override suspend fun error(msg: String) {
        println("${LocalDateTime.now()}: ERROR - $msg")
    }
}
