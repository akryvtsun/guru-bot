package guru

import guru.state.UserId
import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.net.HttpURLConnection
import java.util.*

class MaterialTimerTask(
    val user: UserId,
    val items: List<Item>,
    val client: TelegramClient,
    val blockAction: (UserId) -> Unit
) : TimerTask() {

    companion object {
        val log = KotlinLogging.logger { }
    }

    override fun run() {
        try {
            processItems()
        } catch (e: TelegramApiRequestException) {
            // process bot blocking by user
            if (e.errorCode == HttpURLConnection.HTTP_FORBIDDEN) {
                blockAction(user)
            }
        }
    }

    private fun processItems() {
        log.debug { "Send material items: ${items}" }
        for (item in items) {
            item.send(user, client)
        }
    }
}