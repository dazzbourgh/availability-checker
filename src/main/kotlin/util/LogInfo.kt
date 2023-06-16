package util

import java.time.LocalDateTime

fun interface LogInfo {
    suspend fun info(msg: String)
}

object ConsoleLogInfo : LogInfo {
    override suspend fun info(msg: String) {
        println("${LocalDateTime.now()}: INFO - $msg")
    }
}
