package ramp.robot.achitecture

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import ramp.robot.Robot
import ramp.robot.task.RobotTask
import java.util.concurrent.atomic.AtomicInteger

class ActionServer(private val robot: Robot, private val maxTasksNum: Int) {
    private val tasksChannel = Channel<RobotTask>(1)
    private val workersChannel = Channel<Unit>(maxTasksNum)
    private val taskRunningCounter = AtomicInteger(0)

    suspend fun startRunning() = coroutineScope {
        for (i in 1..maxTasksNum)
            workersChannel.send(Unit)

        startListeningForWork()
    }

    suspend fun sendTask(task: RobotTask) = tasksChannel.send(task)

    fun isAvailable() = taskRunningCounter.get() < maxTasksNum

    private suspend fun startListeningForWork() = withContext(Dispatchers.IO) {
        for (work in workersChannel) {
            val task = try {
                tasksChannel.receive()
            } catch (e: ClosedReceiveChannelException) {
                this.cancel()
                return@withContext
            }

            launch {
                taskRunningCounter.decrementAndGet()
                delay(task.timeToAccomplish)
                taskRunningCounter.incrementAndGet()
                workersChannel.send(Unit)
            }
        }
    }
}