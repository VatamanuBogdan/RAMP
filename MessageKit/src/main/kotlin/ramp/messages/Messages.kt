package ramp.messages

import kotlinx.serialization.Serializable

@Serializable
data class LoginMessage(override val transport: MessageTransport, val robotId: String): Message()

@Serializable
data class LoadingMessage(override val transport: MessageTransport, val loadValue: Long): Message()

@Serializable
data class WorkMessage(
    override val transport: MessageTransport, val senderLoadValue: Long, val task: RobotTask
): Message()

@Serializable
data class WorkResponseMessage(
    override val transport: MessageTransport, val taskId: String, val accepted: Boolean
) : Message()