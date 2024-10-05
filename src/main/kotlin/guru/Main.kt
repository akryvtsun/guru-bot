package guru

import dev.inmo.krontab.doInfinity
import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.meta.exceptions.TelegramApiException


val log = KotlinLogging.logger { }

suspend fun main() {
    log.info { "Starting GuruBot..." }

    val token = System.getenv("BOT_TOKEN")

    val telegramClient = OkHttpTelegramClient(token)
    val state = UsersState()

    val guruBot = GuruBot(state, telegramClient)
    val mailingListJob = MailingListJob(state, telegramClient)

    val botsApi = TelegramBotsLongPollingApplication()
    try {
        botsApi.registerBot(token, guruBot)
    } catch (e: TelegramApiException) {
        log.error(e) { "Error on register bot" }
    }

    doInfinity("/5 * * * *") {
        mailingListJob.run()
    }
}