package guru

import com.google.gson.GsonBuilder
import java.io.File
import java.time.LocalTime

sealed class Item

data class TextItem(val text: String) : Item()
data class ImageItem(val image: String) : Item()
data class VideoItem(val video: String) : Item()

data class Course(val periods: List<Period>) {

    data class Period(val materials: List<Material>) {

        data class Material(
            val time: LocalTime,
            val items: List<Item>
        )
    }
}

internal class CourseConfig(configFile: String) {

    private val config: Course

    val course: List<Course.Period>
        get() = config.periods

    init {
        val jsonString = File(configFile).readText()
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
            .registerTypeAdapter(Item::class.java, ItemDeserializer())
            .create()
        config = gson.fromJson(jsonString, Course::class.java)
    }
}

