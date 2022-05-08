package ramp

import kotlinx.coroutines.delay
import ramp.messages.*

class LoadPublisher {
    var dispatcher: MessageDispatcher? = null

    suspend fun startRunning() {
        // TODO: Replace this temporary routine for testing with the actual implementation
        while (true) {
            val workPublishMessage = WorkPublishMessage(MessageInfo("Robot_Sender1", "Robot_Receiver1"), "Work1");
            dispatcher?.dispatchOutgoingMessage(workPublishMessage)
            delay(1000)
        }
    }
}