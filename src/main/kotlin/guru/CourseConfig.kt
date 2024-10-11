package guru

import com.google.gson.*
import java.io.File
import java.lang.reflect.Type
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

class CourseConfig(configFile: String) {

    private val config: Course

    val course: List<Course.Period>
        get() = config.periods

    init {
        val jsonString = File(configFile).readText()
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalTime::class.java, LocalTimeTypeAdapter())
            .registerTypeAdapter(Item::class.java, itemDeserializer)
            .create()
        config = gson.fromJson(jsonString, Course::class.java)
    }
}

private class LocalTimeTypeAdapter : JsonDeserializer<LocalTime> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalTime {
        return LocalTime.parse(json.asString)
    }
}

val itemDeserializer = JsonDeserializer { json, _, _ ->
    val jsonObject = json.asJsonObject

    when {
        jsonObject.has("text") -> Gson().fromJson(jsonObject, TextItem::class.java)
        jsonObject.has("image") -> Gson().fromJson(jsonObject, ImageItem::class.java)
        jsonObject.has("video") -> Gson().fromJson(jsonObject, VideoItem::class.java)
        else -> throw IllegalArgumentException("Unknown item type")
    }
}