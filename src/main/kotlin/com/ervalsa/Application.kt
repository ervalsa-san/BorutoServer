package com.ervalsa

import com.ervalsa.plugins.*
import io.ktor.application.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureKoin()
    configureDefaultHeader()
    configureSerialization()
    configureMonitoring()
    configureRouting()
}
