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
    fun `register user`() {
        val config = mockk<CourseConfig>()
        val period = Course.Period(listOf(Course.Period.Material(LocalTime.now(), emptyList())))
        every { config.course } returns listOf(period)

        val tgClient = mockk<TelegramClient>()
        val storage = mockk<StateStorage>()
        val slot = slot<Map<UserId, CourseState<List<MaterialTimerTask>>>>()
        every { storage.save(capture(slot)) } just runs

        val state = BotState(config, tgClient, storage)
        state.register(12345)
        state.save()

        assertThat(slot.isCaptured).isTrue
        assertThat(slot.captured.size).isOne()
    }
}