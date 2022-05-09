package ramp.robot.communication

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ramp.messages.*
import ramp.robot.Robot


class ConnectionManager(private val robotId: String,
                        private val address: NetworkAddress,
                        private val dispatcher: MessageDispatcher) {

    private val client: HttpClient = HttpClient {
        install(WebSockets)
    }

    private val outgoingChannel = Channel<Message>()
    private var session: DefaultClientWebSocketSession? = null

    suspend fun startRunning() {
        client.webSocket(HttpMethod.Get, address.host, address.port, address.path) {
            val loginMessage = LoginMessage(MessageTransport(robotId, ""), robotId);
            send(Json.encodeToString(MessageSerializer, loginMessage))

            session = this
            val incomingHandlerJob = launch { handleIncomingMessages() }
            val outgoingHandlerJob = launch { handleOutgoingMessages() }
            incomingHandlerJob.join()
            outgoingHandlerJob.join()
            session = null
        }
        client.close()
    }

    suspend fun stopRunning() {
        outgoingChannel.close()
        session?.close()
    }

    suspend fun sendMessage(message: Message) {
        outgoingChannel.send(message)
    }

    private suspend fun handleIncomingMessages() {
        try {
            for (frame in session!!.incoming) {
                frame as? Frame.Text ?: continue
                val decodedMessage = Json.decodeFromString(MessageSerializer, frame.readText())
                dispatcher.dispatchIncomingMessage(decodedMessage)
            }
        } catch (e: ClosedReceiveChannelException) {
            Robot.logger.error("[$TAG] incoming channel closed while receiving")
        } catch (e: Exception) {
            Robot.logger.error("[$TAG] Exception while receiving: ${e.localizedMessage}")
        }
    }

    private suspend fun handleOutgoingMessages() {
        for (message in outgoingChannel) {
            try {
                session!!.send(Json.encodeToString(MessageSerializer, message))
            } catch (e: Exception) {
                Robot.logger.error("[$TAG] Exception while sending: ${e.localizedMessage}")
            }
        }
    }

    companion object {
        const val TAG = "ConnectionManager"
    }
}