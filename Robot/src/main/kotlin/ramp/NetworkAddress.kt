package ramp

class NetworkAddress(val host: String, val port: Int, val path: String)

val DefaultNetworkAddress = NetworkAddress("127.0.0.1", 8080, "/ramp-communication")