package guru

import com.google.gson.GsonBuilder
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

typealias UserId = Long
typealias Material = String

data class Course(
    val items: List<Item>
) {
    data class Item(
        val time: LocalTime,
        val text: Material
    )
}

interface Registrar {
    fun register(user: UserId)
    fun unregister(user: UserId)
}

class CourseTimerTask(
    val client: TelegramClient,
    val user: UserId,
    val text: String,
    val isLastItem: Boolean
) : TimerTask() {

    override fun run() {
        client.sendMessage(user, text)
        if (isLastItem) {
            client.sendMessage(user, "\u2705 *Курс завершено\\!*\nБажаю гарного дня\\! \uD83D\uDE09")
        }
    }
}

/**
 * Holds users state and progress in course
 */
class CourseState(
    private val configFile: String, private val client: TelegramClient
) : Registrar {

    private val course: Course

    // users course progress
    private val users = mutableMapOf<UserId, MutableList<CourseTimerTask>>()

    private val t = Timer()

    init {
        val jsonString = this.javaClass.getResource(configFile)?.readText()
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalTime::class.java, LocalTimeTypeAdapter())
            .create()
        course = gson.fromJson(jsonString, Course::class.java)
    }

    @Synchronized
    override fun register(user: UserId) {
        users[user] = mutableListOf()

        val now = LocalDateTime.now()
        for (item in course.items) {
            val time = item.time

            val result: LocalDateTime = now
                .plusHours(time.hour.toLong())
                .plusMinutes(time.minute.toLong())
                .plusSeconds(time.second.toLong())

            // Convert LocalDateTime to ZonedDateTime at the system's default time zone
            val zonedDateTime: ZonedDateTime = result.atZone(ZoneId.systemDefault())
            // Convert ZonedDateTime to Instant
            val instant = zonedDateTime.toInstant()
            // Convert Instant to Date
            val date = Date.from(instant)
            val task = CourseTimerTask(client, user, item.text, item == course.items.last())
            users[user]?.add(task)
            t.schedule(task, date)
        }
    }

    @Synchronized
    override fun unregister(user: UserId) {
        users[user]?.forEach {
            it.cancel()
        }
        users -= user
    }
}