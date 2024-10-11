package guru

import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.meta.generics.TelegramClient
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
class CourseState(
    private val config: CourseConfig, private val client: TelegramClient
) : Registrar {

    companion object {
        val isDebug = System.getenv("BOT_DEBUG").toBoolean()

        val log = KotlinLogging.logger { }
    }

    private val timer = Timer()

    // users course items for sending
    private val users = mutableMapOf<UserId, MutableList<MaterialTimerTask>>()

    @Synchronized
    override fun register(user: UserId) {
        val tasks = mutableListOf<MaterialTimerTask>()

        var now = LocalDateTime.now()
        // periods == days for PROD and minutes for DEBUG
        for (period in config.course) {
            now = if (isDebug) now.plusMinutes(1) else now.plusDays(1)
            // material is a list of items need to be posted in the same time
            for (material in period.materials) {
                val task = MaterialTimerTask(user, material.items, client)
                tasks.add(task)
                timer.schedule(task, getTaskDate(now, material.time))
            }
        }

        users[user] = tasks
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
        // if user unsubscribed then cancel all his not executed tasks
        users[user]?.forEach { it.cancel() }
        users -= user

        val cancelled = timer.purge()
        log.debug { "Removed $cancelled tasks from timer queue" }
    }
}

class MaterialTimerTask(
    val user: UserId,
    val items: List<Course.Period.Material.Text>,
    val client: TelegramClient,
) : TimerTask() {

    override fun run() {
        for (item in items) {
            client.sendMessage(user, item.text)
        }
    }
}