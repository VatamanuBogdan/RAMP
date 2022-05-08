package ramp.robot.achitecture

import kotlinx.coroutines.delay
import ramp.Robot
import ramp.messages.MessageTransport
import ramp.messages.WorkPublishMessage
import ramp.robot.communication.MessageDispatcher

class LoadPublisher(private val robot: Robot) {
    var dispatcher: MessageDispatcher? = null

    suspend fun startRunning() {
        // TODO: Replace this temporary routine for testing with the actual implementation
        while (true) {
            val workPublishMessage = WorkPublishMessage(MessageTransport(robot.id, "", true), "Work1");
            dispatcher?.dispatchOutgoingMessage(workPublishMessage)
            delay(1000)
        }
    }
}