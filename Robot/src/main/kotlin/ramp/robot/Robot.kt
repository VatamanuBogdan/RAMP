package ramp

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ramp.robot.RobotMessageDispatcher
import ramp.robot.achitecture.LoadPublisher
import ramp.robot.achitecture.LoadServer
import ramp.robot.communication.ConnectionManager
import ramp.robot.communication.DefaultNetworkAddress


class Robot(val id: String) {

    suspend fun run() = coroutineScope {
        println("Started running: $id")

        val loadServer = LoadServer(this@Robot)
        val loadPublisher = LoadPublisher(this@Robot)
        val dispatcher = RobotMessageDispatcher(loadServer, loadPublisher)

        val connectionManager = ConnectionManager(id, DefaultNetworkAddress, dispatcher)
        dispatcher.connectionManager = connectionManager

        launch { connectionManager.startRunning() }
        launch { loadPublisher.startRunning() }
    }
}