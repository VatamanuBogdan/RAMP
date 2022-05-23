# EPSM-RAMP

[![asciicast](https://asciinema.org/a/496753.svg)](https://asciinema.org/a/496753)

The implementation is based on 3 components, described below.

### Environment

The Enviromnent component is used to simulate a boradcast communication medium.
The robots connect to the enviromnent using WebSockets and can send messages over. The environment
is responsible with routing (broadcasting) the messages.

### MessageKit

MessageKit is a common component that holds the format for serialisation and deserialisation of messages.
The underlying format used is JSON. There are 4 types of messages defined:

 * `LoginMessage` is used by the robots to authenticate when entering the communication enviromnent.
 * `LoadingMessage` is used by robots to broadcast their load factor. The load factor is used to judge whether a new work unit can be taken.
 * `WorkMessage` contains the data necessary to announce a work unit being sent elsewhere. It contains all the necessary data to complete the computational task.
 * `WorkResponseMessage` is used to announce that a robot had successfully started computing a task. The task announcer should stop announcing it elsewhere. 


### Robot

The robots have a fixed list of tasks (configured at startup) that can be run.
The robots broadcast periodically the to the entire network their load factor.

When work is available, the robot checks whether it can execute it locally. If no, and there is 
a robot on the network that is not loaded, it will try to send the work unit periodically over until
a robot acknowledges the task.

### Execution

For the test bench we initialised 2 robots (Zenobia, Claudius)
 * Zenobia has a list of tasks to execute
 * Claudius is free to take any task (has no initial load)

We can see that Zenobia is moving tasks to Claudius as soon as it discovers
that the the peer's load is low.

Zenobia;s log:

```log
$ gradle clean; gradle run --args 'Zenobia tasks1' | tee ~/zenobia.log

> Task :MessageKit:compileKotlin
> Task :MessageKit:compileJava NO-SOURCE

> Task :compileKotlin
w: /home/ux/EPSM-RAMP/Robot/src/main/kotlin/ramp/robot/task/RobotTaskLoader.kt: (9, 6): This class can only be used with the compiler argument '-opt-in=kotlin.RequiresOptIn'

> Task :compileJava NO-SOURCE
> Task :processResources
> Task :classes
> Task :MessageKit:processResources NO-SOURCE
> Task :MessageKit:classes UP-TO-DATE
> Task :MessageKit:inspectClassesForKotlinIC
> Task :MessageKit:jar

> Task :run
21:19:51.950 INFO - Robot started with [Zenobia] id and [tasks1] tasks list
21:19:52.175 INFO - [LoadPublisher] Load Publisher started...
21:19:52.177 INFO - [ActionServer] Action Server started...
21:19:52.179 INFO - [ActionClient] Action Client started...
21:19:55.228 INFO - [ActionClient] Task Task-1-1 was move to Claudius
21:19:55.243 INFO - [ActionClient] Task Task-1-2 was move to Claudius
21:19:55.259 INFO - [ActionClient] Task Task-1-3 was move to Claudius
21:19:55.278 INFO - [ActionClient] Task Task-1-4 was move to Claudius
21:19:55.293 INFO - [ActionClient] Task Task-1-5 was move to Claudius
21:19:55.308 INFO - [ActionClient] Task Task-1-6 was move to Claudius
21:19:55.324 INFO - [ActionClient] Task Task-1-7 was move to Claudius
21:19:55.344 INFO - [ActionClient] Sent task to ActionClient: Task-1-8
21:19:55.361 INFO - [ActionClient] Sent task to ActionClient: Task-1-9
21:19:55.363 INFO - [ActionServer] Started task Task-1-8 with ETA 20000
21:19:55.364 INFO - [ActionServer] Started task Task-1-9 with ETA 20000
21:20:15.364 INFO - [ActionServer] Finished task Task-1-9
21:20:15.364 INFO - [ActionServer] Finished task Task-1-8
```


Claudius's log:

```log
$ gradle clean; gradle run --args 'Claudius task-empty' | tee ~/claudius.log

> Task :MessageKit:compileKotlin
> Task :MessageKit:compileJava NO-SOURCE

> Task :compileKotlin
w: /home/ux/EPSM-RAMP/Robot/src/main/kotlin/ramp/robot/task/RobotTaskLoader.kt: (9, 6): This class can only be used with the compiler argument '-opt-in=kotlin.RequiresOptIn'

> Task :compileJava NO-SOURCE
> Task :processResources
> Task :classes
> Task :MessageKit:processResources NO-SOURCE
> Task :MessageKit:classes UP-TO-DATE
> Task :MessageKit:inspectClassesForKotlinIC
> Task :MessageKit:jar

> Task :run
21:19:42.708 INFO - Robot started with [Claudius] id and [task-empty] tasks list
21:19:42.930 INFO - [LoadPublisher] Load Publisher started...
21:19:42.933 INFO - [ActionServer] Action Server started...
21:19:42.935 INFO - [ActionClient] Action Client started...
21:19:55.210 INFO - [ActionClient] Sent task to ActionClient: Task-1-1
21:19:55.224 INFO - [ActionServer] Started task Task-1-1 with ETA 30000
21:19:55.236 INFO - [ActionClient] Sent task to ActionClient: Task-1-2
21:19:55.236 INFO - [ActionServer] Started task Task-1-2 with ETA 45000
21:19:55.251 INFO - [ActionClient] Sent task to ActionClient: Task-1-3
21:19:55.252 INFO - [ActionServer] Started task Task-1-3 with ETA 10000
21:19:55.269 INFO - [ActionClient] Sent task to ActionClient: Task-1-4
21:19:55.269 INFO - [ActionServer] Started task Task-1-4 with ETA 8000
21:19:55.286 INFO - [ActionClient] Sent task to ActionClient: Task-1-5
21:20:03.270 INFO - [ActionServer] Finished task Task-1-4
21:20:03.271 INFO - [ActionClient] Sent task to ActionClient: Task-1-6
21:20:03.271 INFO - [ActionServer] Started task Task-1-5 with ETA 8000
21:20:05.253 INFO - [ActionServer] Finished task Task-1-3
21:20:05.253 INFO - [ActionClient] Sent task to ActionClient: Task-1-7
21:20:05.254 INFO - [ActionServer] Started task Task-1-6 with ETA 10000
21:20:11.272 INFO - [ActionServer] Finished task Task-1-5
21:20:11.273 INFO - [ActionServer] Started task Task-1-7 with ETA 10000
21:20:15.254 INFO - [ActionServer] Finished task Task-1-6
21:20:21.275 INFO - [ActionServer] Finished task Task-1-7
21:20:25.225 INFO - [ActionServer] Finished task Task-1-1
21:20:40.237 INFO - [ActionServer] Finished task Task-1-2
```


Environment's log:

```log
$ gradle clean; gradle run | tee ~/env.log

> Task :processResources
> Task :MessageKit:processResources NO-SOURCE
> Task :MessageKit:compileKotlin
> Task :MessageKit:compileJava NO-SOURCE
> Task :MessageKit:classes UP-TO-DATE
> Task :MessageKit:inspectClassesForKotlinIC
> Task :MessageKit:jar
> Task :compileKotlin
> Task :compileJava NO-SOURCE
> Task :classes

> Task :run
2022-05-10 21:18:10.826 [main] INFO  ktor.application - Autoreload is disabled because the development mode is off.
2022-05-10 21:18:10.902 [main] INFO  ktor.application - Application auto-reloaded in 0.071 seconds.
2022-05-10 21:18:11.127 [DefaultDispatcher-worker-1] INFO  ktor.application - Responding at http://0.0.0.0:8080
2022-05-10 21:18:49.881 [eventLoopGroupProxy-3-4] INFO  ktor.application - Robot Zenobia logged in successfully
2022-05-10 21:18:59.223 [eventLoopGroupProxy-3-2] INFO  ktor.application - Robot Claudius logged in successfully
2022-05-10 21:19:43.150 [eventLoopGroupProxy-3-6] INFO  ktor.application - Robot Claudius logged in successfully
2022-05-10 21:19:52.441 [eventLoopGroupProxy-3-1] INFO  ktor.application - Robot Zenobia logged in successfully
2022-05-10 21:19:55.199 [eventLoopGroupProxy-3-4] INFO  ktor.application - Routing message Work Message FROM [Zenobia] TO Claudius]
2022-05-10 21:19:55.217 [eventLoopGroupProxy-3-1] INFO  ktor.application - Routing message Response for Work Message FROM [Claudius] TO Zenobia]
2022-05-10 21:19:55.232 [eventLoopGroupProxy-3-5] INFO  ktor.application - Routing message Work Message FROM [Zenobia] TO Claudius]
2022-05-10 21:19:55.239 [eventLoopGroupProxy-3-2] INFO  ktor.application - Routing message Response for Work Message FROM [Claudius] TO Zenobia]
2022-05-10 21:19:55.247 [eventLoopGroupProxy-3-6] INFO  ktor.application - Routing message Work Message FROM [Zenobia] TO Claudius]
2022-05-10 21:19:55.255 [eventLoopGroupProxy-3-3] INFO  ktor.application - Routing message Response for Work Message FROM [Claudius] TO Zenobia]
2022-05-10 21:19:55.264 [eventLoopGroupProxy-3-7] INFO  ktor.application - Routing message Work Message FROM [Zenobia] TO Claudius]
2022-05-10 21:19:55.274 [eventLoopGroupProxy-3-4] INFO  ktor.application - Routing message Response for Work Message FROM [Claudius] TO Zenobia]
2022-05-10 21:19:55.282 [eventLoopGroupProxy-3-1] INFO  ktor.application - Routing message Work Message FROM [Zenobia] TO Claudius]
2022-05-10 21:19:55.289 [eventLoopGroupProxy-3-5] INFO  ktor.application - Routing message Response for Work Message FROM [Claudius] TO Zenobia]
2022-05-10 21:19:55.297 [eventLoopGroupProxy-3-2] INFO  ktor.application - Routing message Work Message FROM [Zenobia] TO Claudius]
2022-05-10 21:19:55.304 [eventLoopGroupProxy-3-6] INFO  ktor.application - Routing message Response for Work Message FROM [Claudius] TO Zenobia]
2022-05-10 21:19:55.311 [eventLoopGroupProxy-3-3] INFO  ktor.application - Routing message Work Message FROM [Zenobia] TO Claudius]
2022-05-10 21:19:55.320 [eventLoopGroupProxy-3-7] INFO  ktor.application - Routing message Response for Work Message FROM [Claudius] TO Zenobia]
2022-05-10 21:19:55.328 [eventLoopGroupProxy-3-4] INFO  ktor.application - Routing message Work Message FROM [Zenobia] TO Claudius]
2022-05-10 21:19:55.337 [eventLoopGroupProxy-3-1] INFO  ktor.application - Routing message Response for Work Message FROM [Claudius] TO Zenobia]
2022-05-10 21:19:55.347 [eventLoopGroupProxy-3-5] INFO  ktor.application - Routing message Work Message FROM [Zenobia] TO Claudius]
2022-05-10 21:19:55.358 [eventLoopGroupProxy-3-2] INFO  ktor.application - Routing message Response for Work Message FROM [Claudius] TO Zenobia]
```
