package guru

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.generics.TelegramClient
import kotlin.test.Test

class MaterialTimerTaskTest {

    @Test
    fun `should consume _start command`() {
        val tgClient = mockk<TelegramClient>()

        val t = MaterialTimerTask(
            user = 1234L,
            items = listOf(TextItem("hello")),
            client = tgClient,
            blockAction = { user -> println(user) }
        )

        every { tgClient.execute(any<SendMessage>()) } returns null

        t.run()

        verify { tgClient.execute(any<SendMessage>()) }
    }

}