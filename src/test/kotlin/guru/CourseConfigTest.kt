package guru

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CourseConfigTest {

    @Test
    fun `load configuration`() {
        val config = CourseConfig("src/test/resources/config.json")

        assertThat(config).isNotNull
    }
}
