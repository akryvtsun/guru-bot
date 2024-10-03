package echo

import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient


class EchoBot(private val client: TelegramClient) : LongPollingSingleThreadUpdateConsumer {

    private val processor = EchoUpdateProcessor()

    override fun consume(update: Update) {
        processor.onUpdateReceived(update)?.let {
            client.execute(it)
        }
    }
}