package echo

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

class EchoBot(token: String) : TelegramLongPollingBot(token) {

    private val processor = EchoUpdateProcessor()

    override fun getBotUsername(): String = System.getenv("BOT_USERNAME")

    override fun onUpdateReceived(update: Update) {
        processor.onUpdateReceived(update)?.let {
            execute(it)
        }
    }
}