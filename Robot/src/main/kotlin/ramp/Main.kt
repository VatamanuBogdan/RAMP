import kotlinx.coroutines.runBlocking
import ramp.Robot

fun main(args: Array<String>) = runBlocking {
        if (args.isEmpty()) {
            println("Invalid Arguments: UUID not found")
            return@runBlocking
        }

        Robot(args[0]).run()
}