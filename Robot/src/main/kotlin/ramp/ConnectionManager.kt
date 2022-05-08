package ramp

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ramp.messages.*


class ConnectionManager(val robotId: String, val address: NetworkAddress, val dispatcher: MessageDispatcher) {
    private val client: HttpClient = HttpClient {
        install(WebSockets)
    }
    private var session: DefaultClientWebSocketSession? = null

    suspend fun startRunning() {
        client.webSocket(HttpMethod.Get, address.host, address.port, address.path) {
            val loginMessage = LoginMessage(MessageTransport(robotId, ""), robotId);
            send(Json.encodeToString(MessageSerializer, loginMessage))

            session = this
            handleIncomingMessages()
            session = null
        }
        client.close()
    }

    suspend fun sendMessage(message: Message) {
        if (session == null)
            return;

        withContext(session!!.coroutineContext) {
            try {
                session!!.send(Json.encodeToString(MessageSerializer, message))
            } catch (e: Exception) {
                println("Error while sending: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun handleIncomingMessages() {
        try {
            for (frame in session!!.incoming) {
                frame as? Frame.Text ?: continue
                val decodedMessage = Json.decodeFromString(MessageSerializer, frame.readText())
                dispatcher.dispatchIncomingMessage(decodedMessage)
            }
        } catch (e: ClosedReceiveChannelException) {
            println("Connection closed")
        } catch (e: Exception) {
            println("Error while receiving: ${e.localizedMessage}")
        }
    }
}