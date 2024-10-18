package guru

import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

private const val STORAGE = "course/state_dump.json"
private val log = KotlinLogging.logger { }

// TODO externalize all text messages sending to users
fun main() {
    log.info { "Starting GuruBot..." }

    val token = System.getenv("BOT_TOKEN")

    val config = CourseConfig("course/config.json")
    val client = OkHttpTelegramClient(token)
    val state = BotState(config, client)

    state.load(STORAGE)
    Runtime.getRuntime().addShutdownHook(Thread {
        state.save(STORAGE)
        // TODO cancel timer
    })

    val bot = GuruBot(state, client)
    try {
        TelegramBotsLongPollingApplication().registerBot(token, bot)
    } catch (e: TelegramApiException) {
        log.error(e) { "Error on register bot" }
    }
}