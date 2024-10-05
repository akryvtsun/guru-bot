package guru

import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

/**
 * Implements bot control via /start and /stop commands
 */
class GuruBot(
    private val state: Registrar,
    private val client: TelegramClient
) : LongPollingSingleThreadUpdateConsumer {

    companion object {
        val log = KotlinLogging.logger { }
    }

    override fun consume(update: Update) {
        if (update.hasMessage() && update.message.hasText()) {
            val user = update.message.chatId
            val text = update.message.text
            log.debug { "Received '$text' message from $user" }

            when (text) {
                "/start" -> {
                    state.register(user)
                    client.sendMessage(user, "\uD83C\uDF89 *Вітаю на курсі* \u203c")
                }
                "/stop" -> {
                    state.unregister(user)
                    client.sendMessage(user, "До побачення\\!")
                }
            }
        }
    }
}