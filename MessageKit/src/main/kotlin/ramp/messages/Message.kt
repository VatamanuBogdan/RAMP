package ramp.messages
import kotlinx.serialization.*

@Serializable
data class MessageTransport(val senderId: String, val receiverId: String, val broadcast: Boolean = false)

@Serializable
sealed class Message {
    abstract val info: MessageTransport
}