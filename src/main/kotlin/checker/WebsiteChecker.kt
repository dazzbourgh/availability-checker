package checker

interface WebsiteChecker {
    suspend fun check(): CheckResult
    val name: String
    val startingUrl: String
}

sealed class CheckResult
object Success : CheckResult()
class Failure(val message: String) : CheckResult()
