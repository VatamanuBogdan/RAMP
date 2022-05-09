package ramp.robot.achitecture

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ramp.messages.MessageTransport
import ramp.messages.RobotTask
import ramp.messages.WorkMessage
import ramp.messages.WorkResponseMessage
import ramp.robot.Robot
import ramp.robot.communication.MessageDispatcher
import java.util.concurrent.atomic.AtomicLong

class ActionClient(private val robot: Robot, private val tasks: List<RobotTask>) {
    private val _localLoad = AtomicLong(-1)
    val localLoad: Long?
        get() { return if (_localLoad.get() != -1L) _localLoad.get() else null }

    private val localTasksChannel = Channel<RobotTask>(CHANNEL_SIZE)
    private val responseMessagesChannel = Channel<WorkResponseMessage>(1)

    var dispatcher: MessageDispatcher? = null

    suspend fun startRunning() = coroutineScope {
        Robot.logger.info("[$TAG] Action Client started...")

        var totalLoading: Long = 0
        for (localTask in tasks) {
            totalLoading += localTask.timeToAccomplish
        }
        _localLoad.set(totalLoading)

        launch { localTasksDistributionRoutine() }
        launch { remoteTasksDistributionRoutine() }
    }

    suspend fun handleWorkResponseMessage(message: WorkResponseMessage) {
        responseMessagesChannel.send(message)
    }

    suspend fun handlerWorkMessage(message: WorkMessage) {
        var accepted = false
        if (_localLoad.get() + message.task.timeToAccomplish < message.senderLoadValue) {
            localTasksChannel.send(message.task)
            _localLoad.addAndGet(message.task.timeToAccomplish)
            accepted = true
        }
        val messageTransport = MessageTransport(robot.id, message.transport.senderId)
        val messageResponse = WorkResponseMessage(messageTransport, message.task.id, accepted)
        dispatcher?.dispatchOutgoingMessage(messageResponse)
    }

    private suspend fun remoteTasksDistributionRoutine() {
        delay(3000)
        for (task in tasks) {
            var bestRobotId: String? = null
            var bestLoadValue = _localLoad.get()

            robot.loadServer.table.forEach { (robotId, loadValue) ->
                if (loadValue < bestLoadValue) { bestRobotId = robotId; bestLoadValue = loadValue }
            }

            if (bestRobotId == null) {
                localTasksChannel.send(task)
                continue
            }

            val workMessage = WorkMessage(MessageTransport(robot.id, bestRobotId!!), _localLoad.get(), task)
            dispatcher?.dispatchOutgoingMessage(workMessage)

            val responseMessage = responseMessagesChannel.receive()
            handleResponseMessage(task, responseMessage)
        }
    }

    private suspend fun handleResponseMessage(task: RobotTask, responseMessage: WorkResponseMessage) {
        if (responseMessage.accepted) {
            _localLoad.addAndGet(-task.timeToAccomplish)
            Robot.logger.info("[$TAG] Task ${responseMessage.taskId} was move to ${responseMessage.transport.senderId}")
            return
        }
        localTasksChannel.send(task)
    }

    private suspend fun localTasksDistributionRoutine() {
        for (task in localTasksChannel) {
            robot.actionServer.sendTask(task)
            _localLoad.addAndGet(-task.timeToAccomplish)
            Robot.logger.info("[$TAG] Sent task to ActionClient: ${task.id}")
        }
    }

    companion object {
        const val TAG = "ActionClient"
        const val CHANNEL_SIZE = 1024
    }
}