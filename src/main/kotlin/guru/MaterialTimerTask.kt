package guru

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
        for (item in items) {
            item.send(user, client)
        }
    }
}