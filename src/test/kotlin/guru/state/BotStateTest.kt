package guru.state

import guru.Course
import guru.CourseConfig
import guru.MaterialTimerTask
import guru.state.BotState.CourseState
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.time.LocalTime

class BotStateTest {

    @Test
    fun `register and unregister users`() {
        val firstUser = 11111L
        val secondUser = 22222L

        val config = mockk<CourseConfig>()
        val period = Course.Period(listOf(Course.Period.Material(LocalTime.now(), emptyList())))
        every { config.course } returns listOf(period)

        val tgClient = mockk<TelegramClient>()
        val storage = mockk<StateStorage>()
        val slot = slot<Map<UserId, CourseState<List<MaterialTimerTask>>>>()
        every { storage.save(capture(slot)) } just runs

        val state = BotState(config, tgClient, storage)
        state.register(firstUser)
        state.register(secondUser)
        state.unregister(firstUser)
        state.save()

        assertThat(slot.isCaptured).isTrue
        val users = slot.captured
        assertThat(users.size).isOne()
        assertThat(users.entries.iterator().next().key).isEqualTo(secondUser)
    }
}