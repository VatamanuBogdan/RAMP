import kotlinx.coroutines.runBlocking
import ramp.robot.Robot

fun main(args: Array<String>) = runBlocking {
        if (args.size != 2) {
            println("Invalid Arguments: Accepted argument form \$robotId \$tasksName")
            return@runBlocking
        }

        Robot(args[0]).run(args[1])
}