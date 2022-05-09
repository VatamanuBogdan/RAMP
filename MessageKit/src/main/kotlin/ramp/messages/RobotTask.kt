package ramp.messages

import kotlinx.serialization.Serializable

@Serializable
data class RobotTask(val id: String, val timeToAccomplish: Long, val processingData: String)