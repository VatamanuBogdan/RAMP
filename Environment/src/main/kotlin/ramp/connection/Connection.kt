package ramp.connection

import io.ktor.websocket.*

class Connection(val robotId: String, val session: DefaultWebSocketSession)
