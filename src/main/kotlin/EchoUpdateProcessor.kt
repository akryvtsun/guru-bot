package echo

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

class EchoUpdateProcessor {

    fun onUpdateReceived(update: Update): SendMessage? {
        if (update.hasMessage() && update.message.hasText()) {
            val text = update.message.text
            val chatId = update.message.chatId

            val sendMessage = SendMessage()
            sendMessage.chatId = chatId.toString()
            sendMessage.text = "You said: $text"

            return sendMessage
        }
        else
            return null
    }
}