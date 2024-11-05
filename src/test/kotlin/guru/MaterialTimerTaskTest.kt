package guru

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.send.SendVideo
import org.telegram.telegrambots.meta.api.objects.ApiResponse
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.net.HttpURLConnection
import kotlin.test.Test

class MaterialTimerTaskTest {

    @Test
    fun `execute timer task`() {
        val tgClient = mockk<TelegramClient>()

        val t = MaterialTimerTask(
            user = 1234L,
            items = listOf(
                TextItem("hello text"),
                ImageItem("course/image.png"),
                VideoItem("course/video.mp4"),
            ),
            client = tgClient,
            blockAction = { user -> println(user) }
        )

        every { tgClient.execute(any<SendMessage>()) } returns null
        every { tgClient.execute(any<SendPhoto>()) } returns null
        every { tgClient.execute(any<SendVideo>()) } returns null

        t.run()

        verify { tgClient.execute(any<SendMessage>()) }
        verify { tgClient.execute(any<SendPhoto>()) }
        verify { tgClient.execute(any<SendVideo>()) }
    }

    @Test
    fun `bot blocking action`() {
        val userId = 1234L
        val tgClient = mockk<TelegramClient>()

        val t = MaterialTimerTask(
            user = userId,
            items = listOf(
                TextItem("hello text"),
                ImageItem("course/image.png"),
                VideoItem("course/video.mp4"),
            ),
            client = tgClient,
            blockAction = { user -> println(user) }
        )

        every { tgClient.execute(any<SendMessage>()) } throws
                TelegramApiRequestException(
                    "test", ApiResponse.builder<String>().errorCode(HttpURLConnection.HTTP_FORBIDDEN).build()
                )
        every { tgClient.execute(any<SendPhoto>()) } returns null
        every { tgClient.execute(any<SendVideo>()) } returns null

        t.run()

        verify { tgClient.execute(any<SendMessage>()) }
        verify(exactly = 0) { tgClient.execute(any<SendPhoto>()) }
        verify(exactly = 0) { tgClient.execute(any<SendVideo>()) }
    }
}