package ramp.robot.achitecture

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ClosedSendChannelException
import ramp.messages.RobotTask
import ramp.robot.Robot
import java.util.concurrent.atomic.AtomicInteger

class ActionServer(private val robot: Robot, private val maxTasksNum: Int) {
    private val tasksChannel = Channel<RobotTask>(1)
    private val workersChannel = Channel<Unit>(maxTasksNum)
    private val taskRunningCounter = AtomicInteger(0)

    suspend fun startRunning() = coroutineScope {
        for (i in 1..maxTasksNum)
            workersChannel.send(Unit)

        Robot.logger.info("[$TAG] Action Server started...")
        startListeningForWork()
        Robot.logger.info("[$TAG] Action Server stopped")
    }

    suspend fun sendTask(task: RobotTask) = tasksChannel.send(task)

    fun stopRunning() = tasksChannel.close()

    fun isAvailable() = taskRunningCounter.get() < maxTasksNum

    private suspend fun startListeningForWork() = withContext(Dispatchers.IO) {
        for (work in workersChannel) {
            val task = try {
                tasksChannel.receive()
            } catch (e: ClosedReceiveChannelException) {
                return@withContext
            }

            launch {
                Robot.logger.info("[$TAG] Started task ${task.id} with ETA ${task.timeToAccomplish}")
                taskRunningCounter.incrementAndGet()
                delay(task.timeToAccomplish)
                Robot.logger.info("[$TAG] Finished task ${task.id}")
                val runningTasks = taskRunningCounter.decrementAndGet()
                if (runningTasks <= 0) {
                    Robot.logger.info("[$TAG] Currently no task in execution. Starving...")
                }

                try { workersChannel.send(Unit) } catch (_: ClosedSendChannelException) { }
            }
        }
    }

    companion object {
        const val TAG = "ActionServer"
    }
}