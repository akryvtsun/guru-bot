package guru

import com.google.gson.*
import java.io.File
import java.lang.reflect.Type
import java.time.LocalTime

data class Course(val periods: List<Period>) {

    data class Period(val materials: List<Material>) {

        data class Material(
            val time: LocalTime,
            val items: List<Text>
        ) {
            data class Text(val text: String)
        }

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