package ramp.messages

import kotlinx.serialization.Serializable

@Serializable
data class WorkPublishMessage(override val info: MessageTransport, val work: String): Message()