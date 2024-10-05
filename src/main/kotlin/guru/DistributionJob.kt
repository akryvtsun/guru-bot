package guru

import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.generics.TelegramClient

/**
 *
 */
class MailingListJob(
    private val state: Preparer,
    private val client: TelegramClient
): Runnable {

    companion object {
        val log = KotlinLogging.logger { }
    }

    override fun run() {
        log.debug { "Scheduled job work..." }

        state.usersSnapshot().forEach {
            val userId = it.first
            sendMessage(userId, it.second)
            if (state.isLastMaterial(userId))
                sendMessage(userId, "Курс завершено! Бажаю гарного дня!")
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