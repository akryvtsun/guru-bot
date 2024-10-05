package guru

import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.generics.TelegramClient

/**
 *
 */
class DistributionJob(
    private val state: Preparer,
    private val client: TelegramClient
): Runnable {

    companion object {
        val log = KotlinLogging.logger { }
    }

    override fun run() {
        log.debug { "Distribution job execution..." }

        state.usersSnapshot().forEach {
            val userId = it.first
            sendMessage(userId, it.second)
            if (state.isLastMaterial(userId))
                sendMessage(userId, "\u2705 *Курс завершено\\!*\nБажаю гарного дня\\! \uD83D\uDE09")
        }
    }

    private fun sendMessage(user: Long, text: String) {
        val message = SendMessage.builder()
            .chatId(user)
            .parseMode(ParseMode.MARKDOWNV2)
            .text(text)
            .build()
        client.execute(message)
    }
}