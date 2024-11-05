package guru.state

import guru.MaterialTimerTask
import guru.state.BotState.CourseState
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDateTime

class StateStorageTest {

    @Test
    fun `load empty state`() {
        val storage = StateStorage("test")

        val state = storage.load()

        assertThat(state)
            .isNotNull()
            .isEmpty()
    }

    @Test
    fun `save empty state`() {
        var userId = 11111L
        val users = mapOf(userId to CourseState(LocalDateTime.now(), emptyList<MaterialTimerTask>()))

        val storageName = "test"
        val storage = StateStorage(storageName)

        storage.save(users)

        val state = File(storageName)
        assertThat(state).exists()
        state.delete()
    }
}