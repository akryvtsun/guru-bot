package guru.state

import guru.MaterialTimerTask
import guru.state.BotState.CourseState
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.time.LocalDateTime

class StateStorageTest {

    @Test
    fun `load state`() {
        val storage = StateStorage("src/test/resources/state_dump.json")

        val state = storage.load()

        assertThat(state).isNotNull()
        assertThat(state.size).isEqualTo(1)
    }

    @Test
    fun `save state`() {
        var userId = 11111L
        val users = mapOf(userId to CourseState(LocalDateTime.now(), emptyList<MaterialTimerTask>()))

        val storageName = "src/test/resources/test_dump.json"
        val storage = StateStorage(storageName)

        storage.save(users)

        val state = File(storageName)
        assertThat(state).exists()
        state.delete()
    }
}