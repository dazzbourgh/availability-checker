package notifier.telegram

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.types.ChatId
import notifier.Notifier

class TelegramNotifier(
    private val bot: TelegramBot,
    private val chatId: ChatId
) : Notifier {
    override suspend fun notify(msg: String) {
        bot.send(chatId, msg)
    }
}
