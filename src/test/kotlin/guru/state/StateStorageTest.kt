package guru.state

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

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
        val storageName = "test"
        val storage = StateStorage(storageName)

        storage.save(emptyMap())

        val state = File(storageName)
        assertThat(state).exists()
        state.delete()
    }
}