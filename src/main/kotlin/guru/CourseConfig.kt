package guru

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalTime

data class Course(val items: List<Item>) {
    data class Item(
        val time: LocalTime,
        val text: String
    )
}

class CourseConfig(configFile: String) {

    private val config: Course

    val course: List<Course.Item>
        get() = config.items

    init {
        val jsonString = this.javaClass.getResource(configFile)?.readText()
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalTime::class.java, LocalTimeTypeAdapter())
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