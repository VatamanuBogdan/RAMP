package ramp.robot

import ramp.messages.LoadingMessage
import ramp.messages.Message
import ramp.messages.WorkMessage
import ramp.messages.WorkResponseMessage
import ramp.robot.achitecture.ActionClient
import ramp.robot.achitecture.LoadPublisher
import ramp.robot.achitecture.LoadServer
import ramp.robot.communication.ConnectionManager
import ramp.robot.communication.MessageDispatcher

class RobotMessageDispatcher(
    var loadServer: LoadServer, var loadPublisher: LoadPublisher,
    var actionClient: ActionClient
) : MessageDispatcher {
    var connectionManager: ConnectionManager? = null

    init {
        loadServer.dispatcher = this
        loadPublisher.dispatcher = this
        actionClient.dispatcher = this
    }

    override suspend fun dispatchIncomingMessage(message: Message) {
        when (message) {
            is LoadingMessage -> loadServer.handleLoadingMessage(message)
            is WorkMessage -> actionClient.handleWorkMessage(message)
            is WorkResponseMessage -> actionClient.handleWorkResponseMessage(message)
            else -> {}
        }
    }

    override suspend fun dispatchOutgoingMessage(message: Message) {
        connectionManager?.sendMessage(message)
    }
}