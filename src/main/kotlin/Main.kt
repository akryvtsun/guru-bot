package echo

import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.meta.exceptions.TelegramApiException


val log = KotlinLogging.logger { }

fun main() {
    log.info { "Starting Guru-bot..." }

    val token = System.getenv("BOT_TOKEN")

    val telegramClient = OkHttpTelegramClient(token)
    val echoBot = EchoBot(telegramClient)
    val botsApi = TelegramBotsLongPollingApplication()

    try {
        botsApi.registerBot(token, echoBot)
    } catch (e: TelegramApiException) {
        log.error(e) { "Error on register bot" }
    }
}