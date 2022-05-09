package ramp.messages

import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object MessageSerializer : JsonContentPolymorphicSerializer<Message>(Message::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        "work" in element.jsonObject -> WorkPublishMessage.serializer()
        "loadValue" in element.jsonObject -> LoadingMessage.serializer()
        "robotId" in element.jsonObject -> LoginMessage.serializer()
        "task" in element.jsonObject -> WorkMessage.serializer()
        "taskId" in element.jsonObject -> WorkResponseMessage.serializer()
        else -> Message.serializer()
    }
}