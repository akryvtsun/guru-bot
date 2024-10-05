import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient
import kotlin.test.Test

// TODO fix it
class GuruBotTest {

    @Test
    fun `should consume _start command`() {
        // given
        val chatId = 12345L
        val textStr = "/start"

        val chat = Chat.builder()
            .type("private")
            .id(chatId)
            .build()
        val message = Message.builder()
            .chat(chat)
            .text(textStr)
            .build()
        val update = Update().apply {
            this.message = message
        }

        val client = mockk<TelegramClient>()
        every { client.execute(any<SendMessage>()) } returns null

        // when
        // GuruBot(client).consume(update)

        // than
        verify { client.execute(any<SendMessage>()) }
    }
}