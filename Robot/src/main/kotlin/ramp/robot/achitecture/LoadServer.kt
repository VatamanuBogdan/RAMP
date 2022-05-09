package ramp.robot.achitecture

import ramp.robot.Robot
import ramp.messages.Message
import ramp.robot.communication.MessageDispatcher

class LoadServer(private val robot: Robot) {
    var dispatcher: MessageDispatcher? = null

    fun handleMessage(message: Message) {
        Robot.logger.info("[$TAG] Message received: $message")
    }

    companion object {
        const val TAG = "LoadServer"
    }
}