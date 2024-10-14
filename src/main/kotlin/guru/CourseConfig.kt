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
            .registerTypeAdapter(LocalTime::class.java, LocalTimeDeserializer())
            .registerTypeAdapter(Item::class.java, ItemDeserializer())
            .create()
        config = gson.fromJson(jsonString, Course::class.java)
    }
}

private class LocalTimeDeserializer : JsonDeserializer<LocalTime> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalTime {
        return LocalTime.parse(json.asString)
    }
}

private class ItemDeserializer : JsonDeserializer<Item> {

    private val gson =  Gson()

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Item {
        val jsonObject = json.asJsonObject
        val clazz = when {
            jsonObject.has("text") -> TextItem::class.java
            jsonObject.has("image") -> ImageItem::class.java
            jsonObject.has("video") -> VideoItem::class.java
            else -> throw IllegalArgumentException("Unknown item type")
        }
        return gson.fromJson(jsonObject, clazz)
    }
}