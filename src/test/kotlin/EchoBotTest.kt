import echo.EchoBot
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient
import kotlin.test.Test

class EchoBotTest {

    @Test
    fun consumeTest() {
        // given
        val chatId = 12345L
        val text = "/start"

        val chat = object : Chat() {}.apply {
            this.id = chatId
        }
        val message = Message().apply {
            this.chat = chat
            this.text = text
        }
        val update = Update().apply {
            this.message = message
        }

        val client = mockk<TelegramClient>()
        every { client.execute(any<SendMessage>()) } returns null

        // when
        EchoBot(client).consume(update)

        // than
        verify { client.execute(any<SendMessage>()) }
    }
}