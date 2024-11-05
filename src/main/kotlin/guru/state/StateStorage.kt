package guru.state

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import guru.LocalDateTimeAdapter
import guru.MaterialTimerTask
import guru.state.BotState.CourseState
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.time.LocalDateTime

class StateStorage(private val storage: String) {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    private companion object {
        val log = KotlinLogging.logger { }
    }

    fun load(deleteAfterLoad: Boolean = true): Map<UserId, CourseState<Int>> {
        var state = emptyMap<UserId, CourseState<Int>>()
        val file = File(storage)
        if (file.exists()) {
            val stateStr = file.bufferedReader().readText()
            val type = object : TypeToken<HashMap<UserId, CourseState<Int>>>() {}.type
            state = gson.fromJson(stateStr, type)
            if (deleteAfterLoad)
                file.delete()
        }
        return state
    }

    fun save(users: Map<UserId, CourseState<List<MaterialTimerTask>>>) {
        val state = mutableMapOf<UserId, CourseState<Int>>()
        for (user in users) {
            val firstActiveTask = user.value.tasks.indexOfFirst { it.cancel() }
            state[user.key] = CourseState(user.value.start, firstActiveTask)
        }
        val stateStr = gson.toJson(state)
        log.debug { "State for saving $stateStr" }
        File(storage).bufferedWriter().use { it.write(stateStr) }
    }
}