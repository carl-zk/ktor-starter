package com.example.config

import com.example.common.Result
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.timeout
import io.ktor.response.respond
import io.ktor.websocket.WebSockets
import java.text.DateFormat
import java.time.Duration

/**
 * @author carl
 *
 */
@JvmOverloads
fun Application.http(testing: Boolean = false) {
    install(DefaultHeaders)
    // This uses use the logger to log every call (request/response)
    install(CallLogging)
    // Automatic '304 Not Modified' Responses
    install(ConditionalHeaders)
    // Supports for Range, Accept-Range and Content-Range headers
    install(PartialContent)
    // This feature enables compression automatically when accepted by the client.
    install(Compression) {
        default()
        excludeContentType(ContentType.Video.Any)
    }
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(5)
        timeout = Duration.ofSeconds(10)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    install(StatusPages) {
        exception<Throwable> { cause ->
            call.application.log.error("un-handled ex -> ", cause)
            call.respond(HttpStatusCode.InternalServerError, Result.fail(599, "whoops, later"))
        }
    }
}