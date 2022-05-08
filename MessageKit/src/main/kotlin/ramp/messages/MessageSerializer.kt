package ramp.messages

import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object MessageSerializer : JsonContentPolymorphicSerializer<Message>(Message::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        "work" in element.jsonObject -> WorkPublishMessage.serializer()
        else -> Message.serializer()
    }
}