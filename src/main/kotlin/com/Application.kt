package com

import com.plugins.configureHTTP
import com.plugins.configureSerialization
import com.plugins.configureDatabases
import com.plugins.configureRouting
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
