package ramp.robot.task

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

@Serializable
data class RobotTask(val name: String, val timeToAccomplish: Long, val processingData: String)

object RobotTaskLoader {
    @OptIn(ExperimentalSerializationApi::class)
    fun deserializedFrom(tasksName: String): List<RobotTask> {
        val inputStream = this::class.java.getResourceAsStream("/tasks/${tasksName}.json")
            ?: throw Exception("Tasks $tasksName not found")
        return Json.decodeFromStream(inputStream)
    }
}

