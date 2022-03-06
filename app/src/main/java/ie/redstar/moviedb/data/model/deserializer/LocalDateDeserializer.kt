package ie.redstar.moviedb.data.model.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateDeserializer : JsonDeserializer<LocalDate?> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDate? {
        return try {
            LocalDate.parse(
                json!!.asJsonPrimitive.asString,
                DateTimeFormatter.ISO_LOCAL_DATE
            )
        } catch (error: Exception) {
            null
        }
    }
}
