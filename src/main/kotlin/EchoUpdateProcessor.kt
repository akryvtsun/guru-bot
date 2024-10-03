package echo

import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

class EchoUpdateProcessor {

    companion object {
        val log = KotlinLogging.logger { }
    }

    private val users = mutableSetOf<Long>()

    fun onUpdateReceived(update: Update): SendMessage? {
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

            return answer?.let {
                val sendMessage = SendMessage()
                sendMessage.chatId = chatId.toString()
                sendMessage.text = answer
                sendMessage
            }
        }
        else
            return null
    }
}