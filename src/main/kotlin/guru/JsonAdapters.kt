package guru

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class LocalTimeDeserializer : JsonDeserializer<LocalTime> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalTime {
        return LocalTime.parse(json.asString)
    }
}

class ItemDeserializer : JsonDeserializer<Item> {

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

class LocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDateTime {
        return LocalDateTime.parse(json.asString)
    }

    override fun serialize(src: LocalDateTime, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}