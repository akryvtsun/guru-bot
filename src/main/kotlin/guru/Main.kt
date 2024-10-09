package guru

import dev.inmo.krontab.doInfinity
import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

val log = KotlinLogging.logger { }

// TODO externalize all text messages sending to users
suspend fun main() {
    log.info { "Starting GuruBot..." }

    val token = System.getenv("BOT_TOKEN")

    val client = OkHttpTelegramClient(token)
    val state = CourseState("/course.json")

    val bot = GuruBot(state, client)
    val job = DistributionJob(state, client)

    try {
        TelegramBotsLongPollingApplication().registerBot(token, bot)
    } catch (e: TelegramApiException) {
        log.error(e) { "Error on register bot" }
    }

    // TODO externalize Cron timetable
    doInfinity("/5 * * * *") {
        job.run()
    }
}