package echo

import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

val log = KotlinLogging.logger { }

fun main() {
    log.info { "Starting Guru-bot..." }
    val token = System.getenv("BOT_TOKEN")
    val echoBot = EchoBot(token)
    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    try {
        botsApi.registerBot(echoBot)
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}