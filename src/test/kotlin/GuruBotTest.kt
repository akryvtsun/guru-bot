import guru.GuruBot
import guru.state.Registrar
import io.mockk.*
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient
import kotlin.test.Test

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

        val registrar = mockk<Registrar>()
        every { registrar.register(chatId) } just runs

        val client = mockk<TelegramClient>()
        every { client.execute(any<SendMessage>()) } returns null

        // when
        GuruBot(registrar, client).consume(update)

        // than
        verify { client.execute(any<SendMessage>()) }
        verify { registrar.register(chatId) }
    }
}