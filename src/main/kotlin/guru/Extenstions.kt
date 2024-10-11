package guru

import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.send.SendVideo
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.io.File

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

fun TelegramClient.sendImage(user: UserId, url: String) {
    val message = SendPhoto.builder()
        .chatId(user)
        .photo(InputFile(File(url)))
        .build()
    execute(message)
}

fun TelegramClient.sendVideo(user: UserId, url: String) {
    val message = SendVideo.builder()
        .chatId(user)
        .video(InputFile(File(url)))
        .build()
    execute(message)
}