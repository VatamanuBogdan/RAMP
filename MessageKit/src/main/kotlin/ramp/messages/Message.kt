package ramp.messages
import kotlinx.serialization.*
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class MessageInfo(val sender: String, val receiver: String, val broadcast: Boolean = false)

@Serializable
sealed class Message {
    abstract val info: MessageInfo
}

@Serializable
data class WorkPublishMessage(override val info: MessageInfo, val work: String): Message()

object MessageSerializer : JsonContentPolymorphicSerializer<Message>(Message::class) {
    override fun selectDeserializer(element: JsonElement) = when {
        "work" in element.jsonObject -> WorkPublishMessage.serializer()
        else -> Message.serializer()
    }
}