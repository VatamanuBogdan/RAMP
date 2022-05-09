package ramp.robot.achitecture

import ramp.messages.LoadingMessage
import ramp.robot.Robot
import ramp.robot.communication.MessageDispatcher
import java.util.*
import kotlin.collections.HashMap

class LoadServer(private val robot: Robot) {
    val table = Collections.synchronizedMap(HashMap<String, Long>())
    var dispatcher: MessageDispatcher? = null

    fun handleLoadingMessage(message: LoadingMessage) {
        table[message.transport.senderId] = message.loadValue
    }

    companion object {
        const val TAG = "LoadServer"
    }
}