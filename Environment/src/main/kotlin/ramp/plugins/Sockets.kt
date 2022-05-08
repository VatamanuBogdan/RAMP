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
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        val connectionRegistry = Collections.synchronizedMap<String, Connection>(HashMap())

        suspend fun loginProcess(session: DefaultWebSocketSession): Connection? {
            try {
                
                val frame = session.incoming.receive()
                if (frame !is Frame.Text) {
                    return null
                }
                val message = Json.decodeFromString(MessageSerializer, frame.readText())
                if (message !is LoginMessage) {
                    return null
                }

                val connection = Connection(message.robotId, session)
                connectionRegistry[message.robotId] = connection
                return connection
            } catch (e: Exception) {
                println("Login error: ${e.localizedMessage}")
            }
            return null
        }

        fun decodeMessageFrom(frame: Frame): Message? {
            if (frame !is Frame.Text) return null
            try {
                return Json.decodeFromString(MessageSerializer, frame.readText())
            } catch (e: Exception) {
                println("Error on message decoding: ${e.localizedMessage}")
            }
            return null
        }

        suspend fun routeFrame(session: DefaultWebSocketSession, currentConnection: Connection, frame: Frame) {
            val message = decodeMessageFrom(frame) ?: return

            val transport = message.transport
            println("Routing message FROM [${transport.senderId}] TO [${if (transport.broadcast) "ALL" else transport.receiverId}]")

            if (!transport.broadcast) {
                connectionRegistry[transport.receiverId]?.session?.send(frame)
                return
            }

            connectionRegistry.forEach {
                if (it.key != currentConnection.robotId) it.value.session.send(frame)
            }
        }

        webSocket("/ramp-communication") {
            val currentConnection = loginProcess(this)
            if (currentConnection == null) {
                println("Login error: Denied Login")
                return@webSocket
            }

            for (frame in incoming) {
                routeFrame(this, currentConnection, frame)
            }
        }
    }
}
