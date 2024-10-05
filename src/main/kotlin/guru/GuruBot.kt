package guru

import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

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
                    sendMessage(user, "Welcome to the Course!")
                }
                "/stop" -> {
                    state.unregister(user)
                    sendMessage(user, "Good Bye!")
                }
            }
        }
    }

    private fun sendMessage(user: Long, text: String) {
        val message = SendMessage.builder()
            .chatId(user)
            .text(text)
            .build()
        client.execute(message)
    }
}