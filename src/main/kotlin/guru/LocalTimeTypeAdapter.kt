package guru

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalTime

class LocalTimeTypeAdapter :  JsonDeserializer<LocalTime> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalTime {
        return LocalTime.parse(json.asString)
    }
}
