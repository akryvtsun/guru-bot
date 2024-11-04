package guru

import guru.state.BotState
import guru.state.StateStorage
import io.github.oshai.kotlinlogging.KotlinLogging
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

// TODO externalize directory for storing course and storing state?
private val log = KotlinLogging.logger { }

// TODO externalize all text messages sending to users
fun main() {
    log.info { "Starting GuruBot..." }

    val token = System.getenv("BOT_TOKEN")

    val client = OkHttpTelegramClient(token)
    val state = BotState(
        CourseConfig("course/config.json"),
        client,
        StateStorage("course/state_dump.json")
    )

    state.load()
    Runtime.getRuntime().addShutdownHook(Thread {
        state.save()
        // TODO cancel timer?
    })

    val bot = GuruBot(state, client)
    try {
        TelegramBotsLongPollingApplication().registerBot(token, bot)
    } catch (e: TelegramApiException) {
        log.error(e) { "Error on register bot" }
    }
}