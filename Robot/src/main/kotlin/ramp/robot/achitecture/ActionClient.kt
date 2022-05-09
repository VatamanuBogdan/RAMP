package ramp.robot.achitecture

import kotlinx.coroutines.coroutineScope
import ramp.robot.Robot
import ramp.robot.task.RobotTask
import java.util.*

class ActionClient(private val robot: Robot, tasks: List<RobotTask>) {
    private val localTasks = Collections.synchronizedList(LinkedList<RobotTask>(tasks))

    suspend fun startRunning() = coroutineScope {
        Robot.logger.info("[$TAG] Action Client started...")
        for (task in localTasks) {
            robot.actionServer.sendTask(task)
            Robot.logger.info("[$TAG] Sent task to ActionClient: ${task.name}")
        }
        Robot.logger.info("[$TAG] Action Client stopped")
    }

    companion object {
        const val TAG = "ActionClient"
    }
}