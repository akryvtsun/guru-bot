import echo.EchoUpdateProcessor
import org.assertj.core.api.Assertions.assertThat
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import kotlin.test.Test

class EchoUpdateProcessorTest {

    @Test
    fun onUpdateReceivedTest() {
        val chatId = 12345L
        val text = "ping"

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

        val output = EchoUpdateProcessor().onUpdateReceived(update)

        assertThat(output)
            .isNotNull()
            .hasFieldOrPropertyWithValue("chatId", chatId.toString())
            .hasFieldOrPropertyWithValue("text", "You said: $text")
    }
}