package notifier

fun interface Notifier {
    suspend fun notify(msg: String)
}
