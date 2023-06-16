package util

interface Log : LogError, LogInfo

object ConsoleLog : Log, LogError by ConsoleLogError, LogInfo by ConsoleLogInfo
