package guru

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.send.SendVideo
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.io.File

interface Item {
    fun send(user: UserId, client: TelegramClient,)
}

data class TextItem(val text: String) : Item {

    override fun send(user: UserId, client: TelegramClient) {
        client.sendMessage(user, text)
    }
}

data class ImageItem(val image: String) : Item {

    override fun send(user: UserId, client: TelegramClient) {
        val message = SendPhoto.builder()
            .chatId(user)
            .photo(InputFile(File(image)))
            .build()
        client.execute(message)
    }
}

data class VideoItem(val video: String) : Item {

    override fun send(user: UserId, client: TelegramClient) {
        val message = SendVideo.builder()
            .chatId(user)
            .video(InputFile(File(video)))
            .build()
        client.execute(message)
    }
}