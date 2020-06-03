package com.example

import com.example.config.http
import com.example.config.kodein
import com.example.config.routers
import io.ktor.application.Application

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@JvmOverloads
fun Application.module(testing: Boolean = false) {
    kodein(testing)
    http(testing)
    routers(testing)
}
