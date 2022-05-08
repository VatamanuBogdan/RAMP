package ramp

import ramp.messages.Message
import ramp.messages.WorkPublishMessage

class LoadServer(private val robot: Robot) {
    var dispatcher: MessageDispatcher? = null

    fun handleMessage(message: Message) {
        when (message) {
            is WorkPublishMessage -> println("Work Publish Message $message")
            else -> println("Message: $message")
        }
    }
}