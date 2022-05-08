package ramp.plugins

import kotlinx.serialization.json.Json
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.routing.*
import ramp.messages.*

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/ramp-communication") {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                when (val message = Json.decodeFromString(MessageSerializer, frame.readText())) {
                    is WorkPublishMessage -> println("Work Publish Message: $message")
                    else -> println("Message $message")
                }
                send(frame)
            }
        }
    }
}
