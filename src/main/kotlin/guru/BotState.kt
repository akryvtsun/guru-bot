package guru

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.io.File
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

typealias UserId = Long

interface Registrar {
    fun register(user: UserId)
    fun unregister(user: UserId)
}

/**
 * Holds users course progress
 */
internal class BotState(
    private val config: CourseConfig, private val client: TelegramClient
) : Registrar {

    companion object {
        val isDebug = System.getenv("BOT_DEBUG").toBoolean()

        val log = KotlinLogging.logger { }
    }

    private data class CourseState<T>(val start: LocalDateTime, val tasks: T)

    // users course items for sending
    private val users = mutableMapOf<UserId, CourseState<List<MaterialTimerTask>>>()

    private val timer = Timer()

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    @Synchronized
    override fun register(user: UserId) {
        log.info { "Register user $user" }
        registerImpl(user, LocalDateTime.now(), 0)
    }

    private fun registerImpl(user: UserId, courseStart: LocalDateTime, firstActiveTask: Int) {
        val tasks = mutableListOf<MaterialTimerTask>()

        var now = courseStart
        var index = 0
        // periods == days for PROD and minutes for DEBUG
        for (period in config.course) {
            now = if (isDebug) now.plusMinutes(1) else now.plusDays(1)
            // material is a list of items need to be posted in the same time
            for (material in period.materials) {
                val task = MaterialTimerTask(user, material.items, client) { unregister(it) }
                if (firstActiveTask == -1 || index++ < firstActiveTask) {
                    task.cancel()
                }
                else {
                    timer.schedule(task, getTaskDate(now, material.time))
                }
                tasks.add(task)
            }
        }

        users[user] = CourseState(courseStart, tasks)
    }

    private fun getTaskDate(period: LocalDateTime, material: LocalTime): Date {
        val timestamp = if (isDebug)
            period
                .withSecond(material.second)
        else
            period
                .withHour(material.hour)
                .withMinute(material.minute)
                .withSecond(material.second)
        // convert LocalDateTime to ZonedDateTime at the system's default time zone
        val zonedDateTime = timestamp.atZone(ZoneId.systemDefault())
        val instant = zonedDateTime.toInstant()
        return Date.from(instant)
    }

    @Synchronized
    override fun unregister(user: UserId) {
        log.info { "Unregister user $user" }
        // if user unsubscribed then cancel all his not executed tasks
        users[user]?.tasks?.forEach { it.cancel() }
        users -= user

        val cancelled = timer.purge()
        log.debug { "Removed $cancelled tasks from timer queue" }
    }

    @Synchronized
    fun load(storage: String) {
        log.info { "Loading state..." }
        val file = File(storage)
        if (file.exists()) {
            val stateStr = file.bufferedReader().readText()
            val type = object : TypeToken<HashMap<UserId, CourseState<Int>>>() {}.type
            val state: Map<UserId, CourseState<Int>> = gson.fromJson(stateStr, type)
            for (user in state) {
                registerImpl(user.key, user.value.start, user.value.tasks)
            }
            file.delete()
        }
    }

    @Synchronized
    fun save(storage: String) {
        log.info { "Saving state..." }
        val state = mutableMapOf<UserId, CourseState<Int>>()
        for (user in users) {
            val firstActiveTask = user.value.tasks.indexOfFirst { it.cancel() }
            state[user.key] = CourseState(user.value.start, firstActiveTask)
        }
        val stateStr = gson.toJson(state)
        File(storage).bufferedWriter().use { it.write(stateStr) }
    }
}

