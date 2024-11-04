package guru

import guru.state.UserId
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.generics.TelegramClient

/**
 * Send rich formatted text to user
 */
fun TelegramClient.sendMessage(user: UserId, text: String) {
    val message = SendMessage.builder()
        .chatId(user)
        .parseMode(ParseMode.MARKDOWNV2)
        .text(text)
        .build()
    execute(message)
}