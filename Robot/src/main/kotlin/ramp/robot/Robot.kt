package ramp.robot

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory;
import ramp.robot.achitecture.ActionClient
import ramp.robot.achitecture.ActionServer
import ramp.robot.achitecture.LoadPublisher
import ramp.robot.achitecture.LoadServer
import ramp.robot.communication.ConnectionManager
import ramp.robot.communication.DefaultNetworkAddress
import ramp.robot.task.RobotTaskLoader

class Robot(val id: String) {
    lateinit var actionServer: ActionServer
    lateinit var actionClient: ActionClient
    lateinit var loadServer: LoadServer
    lateinit var loadPublisher: LoadPublisher

    suspend fun startRunning(tasksName: String) = coroutineScope {
        logger.info("Robot started with [$id] id and [$tasksName] tasks list")

        val tasks = try {
            RobotTaskLoader.deserializedFrom(tasksName)
        } catch (e: Exception) {
            logger.error("Error task loading: ${e.localizedMessage}")
            logger.info("Robot with [$id] stopped")
            return@coroutineScope
        }

        actionServer = ActionServer(this@Robot, 4)
        actionClient = ActionClient(this@Robot, tasks)
        loadServer = LoadServer(this@Robot)
        loadPublisher = LoadPublisher(this@Robot)

        val dispatcher = RobotMessageDispatcher(loadServer, loadPublisher)
        val connectionManager = ConnectionManager(id, DefaultNetworkAddress, dispatcher)
        dispatcher.connectionManager = connectionManager

        launch { connectionManager.startRunning() }
        launch { loadPublisher.startRunning() }
        launch { actionServer.startRunning() }
        launch { actionClient.startRunning() }.join()
        actionServer.stopRunning()
        loadPublisher.stopRunning()
        connectionManager.stopRunning()
    }

    companion object {
        val logger = LoggerFactory.getLogger(Robot::class.java)
    }
}