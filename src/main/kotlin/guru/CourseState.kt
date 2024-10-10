package guru

import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.time.LocalDate
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
    private val users = mutableMapOf<UserId, MutableList<CourseTimerTask>>()

    @Synchronized
    override fun register(user: UserId) {
        val tasks = mutableListOf<CourseTimerTask>()

        for (item in config.course) {
            val task = CourseTimerTask(user, item.text,item == config.course.last(), client)
            tasks.add(task)
            timer.schedule(task, getTaskDate(item.time))
        }

        users[user] = tasks
    }

    private fun getTaskDate(configTime: LocalTime): Date {

        val result =
            if (isDebug) {
                // for debug consider configTime as current time offset
                LocalDateTime.now()
                    .plusHours(configTime.hour.toLong())
                    .plusMinutes(configTime.minute.toLong())
                    .plusSeconds(configTime.second.toLong())
            } else {
                // for prod consider configTime as absolute time for current day
                LocalDateTime.of(LocalDate.now(), configTime)
            }

        // convert LocalDateTime to ZonedDateTime at the system's default time zone
        val zonedDateTime = result.atZone(ZoneId.systemDefault())
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

class CourseTimerTask(
    val user: UserId,
    val text: String,
    val isLastItem: Boolean,
    val client: TelegramClient,
) : TimerTask() {

    override fun run() {
        client.sendMessage(user, text)
        if (isLastItem) {
            client.sendMessage(user, "\u2705 *Курс завершено\\!*\nБажаю гарного дня\\! \uD83D\uDE09")
        }
    }
}