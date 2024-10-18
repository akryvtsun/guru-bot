package guru

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
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
 * Holds users tasks state
 */
internal class CourseState(
    private val config: CourseConfig, private val client: TelegramClient
) : Registrar {

    companion object {
        val isDebug = System.getenv("BOT_DEBUG").toBoolean()

        val log = KotlinLogging.logger { }
    }

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    private val timer = Timer()

    // users course items for sending
    private val users = mutableMapOf<UserId, CourseInfo<MaterialTimerTask>>()

    @Synchronized
    override fun register(user: UserId) {
        log.info { "Register user $user" }
        registerImpl(user, LocalDateTime.now(), emptyList())
    }

    private fun registerImpl(user: UserId, courseStart: LocalDateTime, tasksState: List<Boolean>) {
        val tasks = mutableListOf<MaterialTimerTask>()

        var now = courseStart
        val iter = tasksState.iterator()
        // periods == days for PROD and minutes for DEBUG
        for (period in config.course) {
            now = if (isDebug) now.plusMinutes(1) else now.plusDays(1)
            // material is a list of items need to be posted in the same time
            for (material in period.materials) {
                if (iter.hasNext()) {
                    val isActive = iter.hasNext()
                    if (!isActive) continue
                }
                val task = MaterialTimerTask(user, material.items, client) { unregister(it) }
                tasks.add(task)
                timer.schedule(task, getTaskDate(now, material.time))
            }
        }

        users[user] = CourseInfo(courseStart, tasks)
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
            val type = object : TypeToken<HashMap<UserId, CourseInfo<Boolean>>>() {}.type
            val state: Map<UserId, CourseInfo<Boolean>> = gson.fromJson(stateStr, type)
            for (user in state) {
                registerImpl(user.key, user.value.start, user.value.tasks)
            }
            file.delete()
        }
    }

    @Synchronized
    fun save(storage: String) {
        log.info { "Saving state..." }
        val state = mutableMapOf<UserId, CourseInfo<Boolean>>()
        for (user in users) {
            val tasksState = mutableListOf<Boolean>()
            for (task in user.value.tasks) {
                tasksState.add(task.cancel())
            }
            // TODO store the first not cancelled task index
            state[user.key] = CourseInfo(user.value.start, tasksState)
        }
        val stateStr = gson.toJson(state)
        File(storage).bufferedWriter().use { it.write(stateStr) }
    }
}

private data class CourseInfo<T>(val start: LocalDateTime, val tasks: List<T>)

private class MaterialTimerTask(
    val user: UserId,
    val items: List<Item>,
    val client: TelegramClient,
    val action: (UserId) -> Unit
) : TimerTask() {

    override fun run() {
        try {
            processItems()
        } catch (e: TelegramApiRequestException) {
            // process bot blocking by user
            if (e.errorCode == 403) {
                action(user)
            }
        }
    }

    private fun processItems() {
        for (item in items) {
            when (item) {
                // TODO move this logic into Item classes
                is TextItem -> client.sendMessage(user, item.text)
                is ImageItem -> client.sendImage(user, item.image)
                is VideoItem -> client.sendVideo(user, item.video)
            }
        }
    }
}