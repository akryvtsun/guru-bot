package guru

import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.meta.generics.TelegramClient

/**
 * Cron driven scheduled job for distribution learning materials to subscribed users
 */
class DistributionJob(
    private val state: Preparer,
    private val client: TelegramClient
): Runnable {

    companion object {
        val log = KotlinLogging.logger { }
    }

    override fun run() {
        log.trace { "Distribution job execution..." }

        state.snapshot().forEach {
            val userId = it.first
            client.sendMessage(userId, it.second)
            if (state.isCourseFinished(userId))
                client.sendMessage(userId, "\u2705 *Курс завершено\\!*\nБажаю гарного дня\\! \uD83D\uDE09")
        }
    }
}