package ramp.messages

import kotlinx.serialization.Serializable

@Serializable
data class LoginMessage(override val transport: MessageTransport, val robotId: String): Message()

@Serializable
data class LoadingMessage(override val transport: MessageTransport, val loadValue: Long): Message()

@Serializable
data class WorkPublishMessage(override val transport: MessageTransport, val work: String): Message()