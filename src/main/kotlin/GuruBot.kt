package echo

import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient


class GuruBot(
    private val client: TelegramClient
) : LongPollingSingleThreadUpdateConsumer {

    companion object {
        val log = KotlinLogging.logger { }
    }

    private val users = mutableSetOf<Long>()

    override fun consume(update: Update) {
        if (update.hasMessage() && update.message.hasText()) {
            val text = update.message.text
            val chatId = update.message.chatId
            log.debug { "Received '$text' message from $chatId" }

            val answer = when (text) {
                "/start" -> {
                    users += chatId
                    "Good day!"
                }
                "/stop" -> {
                    users -= chatId
                    "Bye!"
                }
                else -> {
                    if (chatId in users) "You said: $text" else null
                }
            }

            answer?.let {
                log.debug { "Generated '$answer' answer" }
                val message = SendMessage(chatId.toString(), answer)
                client.execute(message)
            }
        }
    }
}