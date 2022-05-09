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
    var loadingValue: Long = -1
    var dispatcher: MessageDispatcher? = null

    fun stopRunning() = mainCoroutineScope.cancel()

    suspend fun startRunning() = coroutineScope {
        try {
            Robot.logger.info("[$TAG] Load Publisher started...")
            mainCoroutineScope = this
            while (true) {
                delay(TIMEOUT)
                if (loadingValue == -1L) {
                    continue
                }

                val loadingMessage = LoadingMessage(MessageTransport(robot.id, "", true), loadingValue);
                dispatcher?.dispatchOutgoingMessage(loadingMessage)
            }
        } finally {
            Robot.logger.info("[$TAG] Load Publisher stopped...")
        }
    }

    companion object {
        const val TAG = "LoadPublisher"
        const val TIMEOUT = 1000L
    }
}