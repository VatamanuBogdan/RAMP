package ramp.robot.communication

import ramp.messages.Message

interface MessageDispatcher {
    fun dispatchIncomingMessage(message: Message)
    suspend fun dispatchOutgoingMessage(message: Message)
}