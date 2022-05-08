package ramp

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ramp.messages.*

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

class Robot(private val UUID: String) {

    suspend fun run() = coroutineScope {
        println("Started running: $UUID")

        val loadServer = LoadServer()
        val loadPublisher = LoadPublisher()
        val dispatcher = RobotMessageDispatcher(loadServer, loadPublisher)

        val connectionManager = ConnectionManager(DefaultNetworkAddress, dispatcher)
        dispatcher.connectionManager = connectionManager

        launch { connectionManager.startRunning() }
        launch { loadPublisher.startRunning() }
    }
}