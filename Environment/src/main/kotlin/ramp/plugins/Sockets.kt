package ramp.plugins

import kotlinx.serialization.json.Json
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.routing.*
import ramp.connection.Connection
import ramp.messages.*
import java.util.*

fun Application.configureSockets() {
    val logger = log

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        val connectionRegistry = Collections.synchronizedMap<String, Connection>(HashMap())

        fun decodeMessageFrom(frame: Frame): Message? {
            if (frame !is Frame.Text) return null
            try {
                return Json.decodeFromString(MessageSerializer, frame.readText())
            } catch (e: Exception) {
                logger.error("Error on message decoding: ${e.localizedMessage}")
            }
            return null
        }

        suspend fun loginProcess(session: DefaultWebSocketSession): Connection? {
            try {
                val message = decodeMessageFrom(session.incoming.receive())
                if (message == null || message !is LoginMessage) {
                    return null
                }
                if (connectionRegistry.containsKey(message.robotId)) {
                    logger.warn("Robot already logged in")
                    return null
                }

                val connection = Connection(message.robotId, session)
                connectionRegistry[message.robotId] = connection
                logger.info("Robot ${message.robotId} logged in successfully")
                return connection
            } catch (e: Exception) {
                logger.error("Login error: ${e.localizedMessage}")
            }
            return null
        }

        fun logMessage(message: Message) {
            if (message is LoadingMessage) {
                return
            }

            val messageType = when (message) {
                is WorkMessage -> "Work Message"
                is WorkResponseMessage -> "Response for Work Message"
                else -> "Message"
            }

            val transport = message.transport
            val receiver = if (transport.broadcast) "ALL" else transport.receiverId
            logger.info("Routing message $messageType FROM [${transport.senderId}] TO $receiver]")
        }

        suspend fun routeFrame(currentConnection: Connection, frame: Frame) {
            val message = decodeMessageFrom(frame) ?: return
            logMessage(message)

            val transport = message.transport
            if (!transport.broadcast) {
                connectionRegistry[transport.receiverId]?.session?.send(frame)
                return
            }
            connectionRegistry.forEach {
                if (it.key != currentConnection.robotId) it.value.session.send(frame)
            }
        }

        webSocket("/ramp-communication") {
            var currentConnection: Connection? = null
            try {
                currentConnection = loginProcess(this)
                if (currentConnection == null) {
                    logger.info("Login denied")
                    return@webSocket
                }
                for (frame in incoming) {
                    routeFrame(currentConnection, frame)
                }
            } finally {
                if (currentConnection != null) {
                    connectionRegistry.remove(currentConnection.robotId)
                }
            }
        }
    }
}
