package ramp.robot

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory;
import ramp.robot.achitecture.LoadPublisher
import ramp.robot.achitecture.LoadServer
import ramp.robot.communication.ConnectionManager
import ramp.robot.communication.DefaultNetworkAddress
import ramp.robot.task.RobotTaskLoader

class Robot(val id: String) {

    suspend fun run(tasksName: String) = coroutineScope {
        logger.info("Robot started with [$id] id and [$tasksName] tasks list")
        println("Started running: $id")

        val tasks = RobotTaskLoader.deserializedFrom(tasksName)
        println(tasks)

        val loadServer = LoadServer(this@Robot)
        val loadPublisher = LoadPublisher(this@Robot)
        val dispatcher = RobotMessageDispatcher(loadServer, loadPublisher)

        val connectionManager = ConnectionManager(id, DefaultNetworkAddress, dispatcher)
        dispatcher.connectionManager = connectionManager

        launch { connectionManager.startRunning() }
        launch { loadPublisher.startRunning() }
    }

    companion object {
        val logger = LoggerFactory.getLogger(Robot::class.java)
    }
}