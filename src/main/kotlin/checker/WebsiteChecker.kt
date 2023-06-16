package checker

interface WebsiteChecker {
    suspend fun check(): Boolean
    val name: String
    val startingUrl: String
}
