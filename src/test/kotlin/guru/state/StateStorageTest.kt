package guru.state

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StateStorageTest {

    @Test
    fun `load empty state`() {
        val storage = StateStorage("test")

        val state = storage.load()

        assertThat(state)
            .isNotNull()
            .isEmpty()
    }
}