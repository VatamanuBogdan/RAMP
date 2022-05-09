package ramp.robot.achitecture

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import ramp.messages.LoadingMessage
import ramp.robot.Robot
import ramp.messages.MessageTransport
import ramp.robot.communication.MessageDispatcher

class LoadPublisher(private val robot: Robot) {
    private lateinit var mainCoroutineScope: CoroutineScope
    var dispatcher: MessageDispatcher? = null

    suspend fun startRunning() = coroutineScope {
        try {
            Robot.logger.info("[$TAG] Load Publisher started...")
            mainCoroutineScope = this
            while (true) {
                val localLoadValue = robot.actionClient.localLoad
                delay(TIMEOUT)
                if (localLoadValue == null) {
                    continue
                }
                val messageTransport = MessageTransport(robot.id, "", true)
                val loadingMessage = LoadingMessage(messageTransport, localLoadValue)
                dispatcher?.dispatchOutgoingMessage(loadingMessage)
            }
        } finally {
            Robot.logger.info("[$TAG] Load Publisher stopped...")
        }
    }

    fun stopRunning() = mainCoroutineScope.cancel()

    companion object {
        const val TAG = "LoadPublisher"
        const val TIMEOUT = 200L
    }
}