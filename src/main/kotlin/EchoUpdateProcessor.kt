package echo

import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

class EchoUpdateProcessor {

    companion object {
        val log = KotlinLogging.logger { }
    }

    fun onUpdateReceived(update: Update): SendMessage? {
        if (update.hasMessage() && update.message.hasText()) {
            val text = update.message.text
            val chatId = update.message.chatId
            log.debug { "Received '$text' message from $chatId" }

            val sendMessage = SendMessage()
            sendMessage.chatId = chatId.toString()
            sendMessage.text = "You said: $text"

            return sendMessage
        }
        else
            return null
    }
}