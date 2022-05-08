package ramp.robot

import ramp.messages.Message
import ramp.robot.achitecture.LoadPublisher
import ramp.robot.achitecture.LoadServer
import ramp.robot.communication.ConnectionManager
import ramp.robot.communication.MessageDispatcher

class RobotMessageDispatcher(
    var loadServer: LoadServer, var loadPublisher: LoadPublisher
) : MessageDispatcher {
    var connectionManager: ConnectionManager? = null

    init {
        loadServer.dispatcher = this
        loadPublisher.dispatcher = this
    }

    override fun dispatchIncomingMessage(message: Message) {
        loadServer.handleMessage(message)
    }

    override suspend fun dispatchOutgoingMessage(message: Message) {
        connectionManager?.sendMessage(message)
    }
}